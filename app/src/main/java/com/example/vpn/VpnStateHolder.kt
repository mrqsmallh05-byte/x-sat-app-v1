package com.example.vpn

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object VpnStateHolder {

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    private val _connectionStep = MutableStateFlow("")
    val connectionStep: StateFlow<String> = _connectionStep.asStateFlow()

    private val _queueNumber = MutableStateFlow(0)
    val queueNumber: StateFlow<Int> = _queueNumber.asStateFlow()

    private val _bytesReceived = MutableStateFlow(0L)
    val bytesReceived: StateFlow<Long> = _bytesReceived.asStateFlow()

    private val _bytesSent = MutableStateFlow(0L)
    val bytesSent: StateFlow<Long> = _bytesSent.asStateFlow()

    private val _currentSpeedMbps = MutableStateFlow(0.0)
    val currentSpeedMbps: StateFlow<Double> = _currentSpeedMbps.asStateFlow()

    private val _activeCarrier = MutableStateFlow("Monogoto Satellite L-Band (Sudan Orbit)")
    val activeCarrier: StateFlow<String> = _activeCarrier.asStateFlow()

    private val _remainingMb = MutableStateFlow(200L)
    val remainingMb: StateFlow<Long> = _remainingMb.asStateFlow()

    private val _consumedMb = MutableStateFlow(0.0)
    val consumedMb: StateFlow<Double> = _consumedMb.asStateFlow()

    // Custom Data Usage Alert states (Fixed MB threshold)
    private val _isAlertEnabled = MutableStateFlow(true)
    val isAlertEnabled: StateFlow<Boolean> = _isAlertEnabled.asStateFlow()

    private val _alertThresholdMb = MutableStateFlow(20)
    val alertThresholdMb: StateFlow<Int> = _alertThresholdMb.asStateFlow()

    private val _isLowBalanceAlertTriggered = MutableStateFlow(false)
    val isLowBalanceAlertTriggered: StateFlow<Boolean> = _isLowBalanceAlertTriggered.asStateFlow()

    // Percentage Milestone Alerts (50%, 75%, 90%)
    private val _is50AlertEnabled = MutableStateFlow(true)
    val is50AlertEnabled: StateFlow<Boolean> = _is50AlertEnabled.asStateFlow()

    private val _is75AlertEnabled = MutableStateFlow(true)
    val is75AlertEnabled: StateFlow<Boolean> = _is75AlertEnabled.asStateFlow()

    private val _is90AlertEnabled = MutableStateFlow(true)
    val is90AlertEnabled: StateFlow<Boolean> = _is90AlertEnabled.asStateFlow()

    private val _triggeredMilestones = MutableStateFlow<Set<Int>>(emptySet())
    val triggeredMilestones: StateFlow<Set<Int>> = _triggeredMilestones.asStateFlow()

    fun setConnected(connected: Boolean) {
        _isConnected.value = connected
        if (!connected) {
            _isConnecting.value = false
            _connectionStep.value = ""
            _currentSpeedMbps.value = 0.0
        }
    }

    fun setConnecting(connecting: Boolean, step: String = "", queueNum: Int = 0) {
        _isConnecting.value = connecting
        _connectionStep.value = step
        _queueNumber.value = queueNum
    }

    fun updateMetrics(rx: Long, tx: Long, speed: Double, remaining: Long, consumed: Double) {
        _bytesReceived.value = rx
        _bytesSent.value = tx
        _currentSpeedMbps.value = speed
        _remainingMb.value = remaining
        _consumedMb.value = consumed

        if (remaining > _alertThresholdMb.value) {
            _isLowBalanceAlertTriggered.value = false
        }
    }

    fun setCarrier(carrier: String) {
        _activeCarrier.value = carrier
    }

    fun setAlertConfig(thresholdMb: Int, enabled: Boolean) {
        _alertThresholdMb.value = thresholdMb
        _isAlertEnabled.value = enabled
        if (_remainingMb.value > thresholdMb) {
            _isLowBalanceAlertTriggered.value = false
        }
    }

    fun setPercentageAlertsConfig(enable50: Boolean, enable75: Boolean, enable90: Boolean) {
        _is50AlertEnabled.value = enable50
        _is75AlertEnabled.value = enable75
        _is90AlertEnabled.value = enable90
    }

    fun markMilestoneTriggered(percent: Int) {
        _triggeredMilestones.value = _triggeredMilestones.value + percent
    }

    fun resetMilestones() {
        _triggeredMilestones.value = emptySet()
    }

    fun setLowBalanceAlertTriggered(triggered: Boolean) {
        _isLowBalanceAlertTriggered.value = triggered
    }
}
