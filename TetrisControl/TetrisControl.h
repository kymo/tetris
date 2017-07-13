/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class TetrisControl */
#include "sapi.h"
#include <windows.h>
#include <stdio.h>
#include <string.h>
#include <atlbase.h>
#include <atlcom.h>
#include "sphelper.h"
#include <iostream>
using namespace std;

#pragma comment(lib,"ole32.lib")  //coinitialize cocreateinstance需要调用ole32.dll
#pragma comment(lib,"sapi.lib")   //sapi.lib在sdk的lib目录，必须正确配置

#define XML_FILE                    "voice.xml"
#define INIT_ERROR                    -1
#define INIT_OK                        0
#define CREATE_SHARED_ENGINE_ERROR  1
#define CREATE_RECO_CONTEXT_ERROR   2
#define CREATE_NOTIFY_MSG_ERROR        3
#define CREATE_INTEREST_ERROR        4
#define CREATE_GRAMMAR_ERROR        5
#define LOAD_XML_FILE_ERROR            6
#define SET_RULE_STATE_ERROR        7
#define MOVE_LEFT L"向左"
#define MOVE_RIGHT L"向右"
#define MOVE_DOWN L"向下"
#define TRANSFOR L"变形"
#define GAME_OVER L"游戏结束"
WCHAR *gameCmd[6] = {MOVE_LEFT, MOVE_RIGHT, MOVE_DOWN, TRANSFOR, GAME_OVER};

#ifndef _Included_TetrisControl
#define _Included_TetrisControl
#ifdef __cplusplus
extern "C" {
#endif
#undef TetrisControl_MIN_PRIORITY
#define TetrisControl_MIN_PRIORITY 1L
#undef TetrisControl_NORM_PRIORITY
#define TetrisControl_NORM_PRIORITY 5L
#undef TetrisControl_MAX_PRIORITY
#define TetrisControl_MAX_PRIORITY 10L
/*
 * Class:     TetrisControl
 * Method:    beginVR
 * Signature: (Ltest;)I
 */
JNIEXPORT jint JNICALL Java_TetrisControl_beginVR
  (JNIEnv *, jclass, jobject);

#ifdef __cplusplus
}
#endif
#endif

