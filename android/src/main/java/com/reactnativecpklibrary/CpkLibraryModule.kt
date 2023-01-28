package com.reactnativecpklibrary

import android.app.Activity
import android.content.Intent
import com.facebook.react.bridge.*
import com.reactnativecpklibrary.route.*

class CpkLibraryModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext),
  ActivityEventListener {
  lateinit var promise: Promise;
  private var helperObject = CompassKernelUIController.CompassHelper(reactApplicationContext);

  private val consumerDeviceApiRoute: ConsumerDeviceAPIRoute by lazy {
    ConsumerDeviceAPIRoute(reactContext, currentActivity)
  }
  private val registerUserWithBiometricsAPIRoute: RegisterUserWithBiometricsAPIRoute by lazy {
    RegisterUserWithBiometricsAPIRoute(reactContext, currentActivity, helperObject)
  }
  private val registerBasicUserAPIRoute: RegisterBasicUserAPIRoute by lazy {
    RegisterBasicUserAPIRoute(reactContext, currentActivity)
  }
  private val consumerDevicePasscodeAPIRoute: ConsumerDevicePasscodeAPIRoute by lazy {
    ConsumerDevicePasscodeAPIRoute(reactContext, currentActivity)
  }
  private val biometricConsentAPIRoute: BiometricConsentAPIRoute by lazy {
    BiometricConsentAPIRoute(reactContext, currentActivity)
  }

  override fun getName(): String {
      return "CpkLibrary"
  }

  init {
    super.initialize()
    reactApplicationContext.addActivityEventListener(this)
  }

  @ReactMethod
  fun saveBiometricConsent(SaveBiometricConsentParams: ReadableMap, promise: Promise){
    this.promise = promise

    biometricConsentAPIRoute.startBiometricConsentIntent(SaveBiometricConsentParams)
  }

  @ReactMethod
  fun getWritePasscode(WritePasscodeParams: ReadableMap, promise: Promise){
    this.promise = promise

    consumerDevicePasscodeAPIRoute.startWritePasscodeIntent(WritePasscodeParams)
  }

  @ReactMethod
  fun getWriteProfile(WriteProfileParams: ReadableMap, promise: Promise){
    this.promise = promise

    consumerDeviceApiRoute.startWriteProfileIntent(WriteProfileParams)
  }

  @ReactMethod
  fun getRegisterBasicUser(RegisterBasicUserParams: ReadableMap, promise: Promise){
    this.promise = promise

  registerBasicUserAPIRoute.startRegisterBasicUserIntent(RegisterBasicUserParams);
  }

  @ReactMethod
  fun getRegisterUserWithBiometrics(RegisterUserWithBiometricsParams: ReadableMap, promise: Promise) {
    this.promise = promise

    registerUserWithBiometricsAPIRoute.startRegisterUserWithBiometricsIntent(
      RegisterUserWithBiometricsParams
    );
  }


  /**@ReactMethod
  fun initBioRegistration(programGuid: String, reliantAppGuid: String, promise: Promise){
    this.promise = promise
    val connectIntent = Intent(reactApplicationContext, CpkBioRegistrationActivity::class.java)
    connectIntent.putExtra("reliantAppGuid", reliantAppGuid);
    connectIntent.putExtra("programGuid", programGuid);
    currentActivity?.startActivityForResult(connectIntent, 2)
  }**/

  override fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {
    when(requestCode){
      in BiometricConsentAPIRoute.REQUEST_CODE_RANGE -> handleApiRouteResponse(requestCode, resultCode, data)
      in ConsumerDeviceAPIRoute.REQUEST_CODE_RANGE -> handleApiRouteResponse(requestCode, resultCode, data)
      in ConsumerDevicePasscodeAPIRoute.REQUEST_CODE_RANGE -> handleApiRouteResponse(requestCode, resultCode, data)
      in RegisterUserWithBiometricsAPIRoute.REQUEST_CODE_RANGE -> handleApiRouteResponse(requestCode, resultCode, data)
      in RegisterBasicUserAPIRoute.REQUEST_CODE_RANGE -> handleApiRouteResponse(requestCode, resultCode, data)
    }
  }

  override fun onNewIntent(p0: Intent?) {
    TODO("Not yet implemented")
  }


  private fun handleApiRouteResponse(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    when (requestCode) {
      BiometricConsentAPIRoute.BIOMETRIC_CONSENT_REQUEST_CODE -> biometricConsentAPIRoute.handleBiometricConsentIntentResponse(resultCode, data, this.promise)
      ConsumerDeviceAPIRoute.WRITE_PROFILE_REQUEST_CODE -> consumerDeviceApiRoute.handleWriteProfileIntentResponse(resultCode, data, this.promise)
      ConsumerDevicePasscodeAPIRoute.WRITE_PASSCODE_REQUEST_CODE -> consumerDevicePasscodeAPIRoute.handleWritePasscodeIntentResponse(resultCode, data, this.promise)
      RegisterUserWithBiometricsAPIRoute.REGISTER_BIOMETRICS_REQUEST_CODE -> registerUserWithBiometricsAPIRoute.handleRegisterUserWithBiometricsIntentResponse(resultCode, data, this.promise)
      RegisterBasicUserAPIRoute.REGISTER_BASIC_USER_REQUEST_CODE -> registerBasicUserAPIRoute.handleRegisterBasicUserIntentResponse(resultCode, data, this.promise)
    }
  }
}
