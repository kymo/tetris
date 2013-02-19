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
	hr = m_cpRecoEngine.CoCreateInstance(CLSID_SpSharedRecognizer);//��������ʶ������
	if(FAILED(hr))
		return CREATE_SHARED_ENGINE_ERROR;
	hr = m_cpRecoEngine->CreateRecoContext( &cpRecoCtxt );//��������ʶ��������
	if(FAILED(hr))
		return  CREATE_RECO_CONTEXT_ERROR;
	//����֪ͨ��Ϣ����
	hr = cpRecoCtxt->SetNotifyWin32Event ();
	if(FAILED(hr))
		return CREATE_NOTIFY_MSG_ERROR;
	hr = cpRecoCtxt->SetInterest(SPFEI(SPEI_RECOGNITION),SPFEI(SPEI_RECOGNITION));//��������Ȥ���¼�
	if(FAILED(hr))
		return CREATE_INTEREST_ERROR;
	hr = cpRecoCtxt->CreateGrammar(33333, &cpGrammar);//�����﷨����
	if(FAILED(hr))
		return CREATE_GRAMMAR_ERROR;
	//��������֮ǰע��Ҫ��xml��ʽ���ļ���������ת��
	WCHAR wszXMLFile[20] = L"";
	MultiByteToWideChar(CP_ACP, 0, (LPCSTR)XML_FILE  , -1, wszXMLFile, 256);
	hr = cpGrammar->LoadCmdFromFile(wszXMLFile,SPLO_DYNAMIC);
	if(FAILED(hr))
		return LOAD_XML_FILE_ERROR;
	//�˴������е�ģ������Ҫȷ��һ��
	hr = cpGrammar->SetRuleState(NULL, NULL, SPRS_ACTIVE );//����
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
        const WCHAR* pchStop = L"����ʶ��";
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
					//ʶ��ɹ�ĳһ������
					if(_wcsicmp(dstrText, gameCmd[i]) == 0)
						break;
				switch(i)
				{
					//����������
					case 0:
						env->CallVoidMethod(_obj, mid_left);
						break;
					//����������
					case 1:
						env->CallVoidMethod(_obj, mid_right);
						break;
					//����������
					case 2:
						env->CallVoidMethod(_obj, mid_down);
						break;
					//�����Ǳ���
					case 3:
						env->CallVoidMethod(_obj, mid_trans);
						break;
					//�����ǽ���
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