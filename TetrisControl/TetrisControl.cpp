#include "TetrisControl.h"

inline HRESULT BlockForResult(ISpRecoContext * pRecoCtxt, ISpRecoResult ** ppResult){
    HRESULT hr = S_OK;
    CSpEvent event;
    while (SUCCEEDED(hr) &&
           SUCCEEDED(hr = event.GetFrom(pRecoCtxt)))
    {
		printf("%d", event.eEventId);
        if(hr == S_OK)
			break;
		hr = pRecoCtxt->WaitForNotifyEvent(INFINITE);
    }
    *ppResult = event.RecoResult();
    if (*ppResult)
    {
        (*ppResult)->AddRef();
    }
    return hr;
}

int initSAPI(CComPtr<ISpRecognizer> &m_cpRecoEngine,CComPtr<ISpRecoContext> &cpRecoCtxt,
		CComPtr<ISpRecoGrammar> &cpGrammar)
{
	HRESULT hr=S_OK;
	hr = m_cpRecoEngine.CoCreateInstance(CLSID_SpSharedRecognizer);//创建语音识别引擎
	if(FAILED(hr))
		return CREATE_SHARED_ENGINE_ERROR;
	hr = m_cpRecoEngine->CreateRecoContext( &cpRecoCtxt );//创建语音识别上下文
	if(FAILED(hr))
		return  CREATE_RECO_CONTEXT_ERROR;
	//创建通知消息机制
	hr = cpRecoCtxt->SetNotifyWin32Event ();
	if(FAILED(hr))
		return CREATE_NOTIFY_MSG_ERROR;
	hr = cpRecoCtxt->SetInterest(SPFEI(SPEI_RECOGNITION),SPFEI(SPEI_RECOGNITION));//创建感兴趣的事件
	if(FAILED(hr))
		return CREATE_INTEREST_ERROR;
	hr = cpRecoCtxt->CreateGrammar(33333, &cpGrammar);//创建语法命令
	if(FAILED(hr))
		return CREATE_GRAMMAR_ERROR;
	//加在命令之前注意要将xml格式的文件进行字码转换
	WCHAR wszXMLFile[20] = L"";
	MultiByteToWideChar(CP_ACP, 0, (LPCSTR)XML_FILE  , -1, wszXMLFile, 256);
	hr = cpGrammar->LoadCmdFromFile(wszXMLFile,SPLO_DYNAMIC);
	if(FAILED(hr))
		return LOAD_XML_FILE_ERROR;
	//此处参数有点模糊，需要确定一下
	hr = cpGrammar->SetRuleState(NULL, NULL, SPRS_ACTIVE );//激活
	if(FAILED(hr))
		return SET_RULE_STATE_ERROR;
	return INIT_OK;
}

JNIEXPORT jint JNICALL Java_TetrisControl_beginVR(JNIEnv *env, jclass cls,jobject _obj)
{
	HRESULT hr = E_FAIL;
    bool fUseTTS = true;            // turn TTS play back on or off
    bool fReplay = true;            // turn Audio replay on or off

	/*
	NOW	 the _ojb stands for the class TetrisUI
	because we need to change the position of the block,
	so we have to use the TetrisUI class as a param that we can
	change the position in real  time
	*/
	jclass cls_tetrisui = env->GetObjectClass(_obj);
	jmethodID mid_left = env->GetMethodID(cls_tetrisui, "dealWithLeft", "()V");
	jmethodID mid_right = env->GetMethodID(cls_tetrisui, "dealWithRight", "()V");
	jmethodID mid_down = env->GetMethodID(cls_tetrisui, "dealWithDown", "()V");
	jmethodID mid_trans = env->GetMethodID(cls_tetrisui, "dealWithTrans", "()V");
	jmethodID mid_gameover = env->GetMethodID(cls_tetrisui, "dealWithGameOver", "()V");
	jmethodID mid_getState = env->GetMethodID(cls_tetrisui, "getVoiceState", "()I");
	hr = ::CoInitialize(NULL);
	if (SUCCEEDED(hr))
    {
		CComPtr<ISpRecognizer> m_cpRecoEngine;
		CComPtr<ISpRecoContext> cpRecoCtxt;
		CComPtr<ISpRecoGrammar> cpGrammar;
		//init sapi
		int ret;
		if((ret = initSAPI(m_cpRecoEngine, cpRecoCtxt, cpGrammar) != INIT_OK))
			return ret;
        USES_CONVERSION;
        const WCHAR* pchStop = L"结束识别";
        CComPtr<ISpRecoResult> cpResult;
        while (SUCCEEDED(hr = BlockForResult(cpRecoCtxt, &cpResult)))
        {	
			//if the user close the recognition engine
			//then quit the process
			jint temp = env->CallIntMethod(_obj, mid_getState);
			if(temp == 0)
				break;

            cpGrammar->SetRuleState(NULL, NULL, SPRS_ACTIVE);
            CSpDynamicString dstrText;
            if (SUCCEEDED(cpResult->GetText(SP_GETWHOLEPHRASE, SP_GETWHOLEPHRASE, 
                                            TRUE, &dstrText, NULL)))
            {
				int i;
                for(i = 0; i < 5; i ++)
					//识别成功某一个命令
					if(_wcsicmp(dstrText, gameCmd[i]) == 0)
						break;
				switch(i)
				{
					//命令是向左
					case 0:
						env->CallVoidMethod(_obj, mid_left);
						break;
					//命令是向右
					case 1:
						env->CallVoidMethod(_obj, mid_right);
						break;
					//命令是向下
					case 2:
						env->CallVoidMethod(_obj, mid_down);
						break;
					//命令是变形
					case 3:
						env->CallVoidMethod(_obj, mid_trans);
						break;
					//命令是结束
					case 4:
						env->CallVoidMethod(_obj, mid_gameover);
						break;
					default:
						break;
				}
				cpResult.Release();
            }
            if (_wcsicmp(dstrText, pchStop) == 0)
                break;
            cpGrammar->SetRuleState(NULL, NULL, SPRS_ACTIVE);
        }
		::CoUninitialize();
    }
}