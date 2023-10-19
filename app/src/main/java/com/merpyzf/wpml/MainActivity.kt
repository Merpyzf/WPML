package com.merpyzf.wpml

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.merpyzf.lib.wpml.WPML

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        make()
    }

    private fun make() {
        WPML.Waypoint().make {
            missionConfig {
                flyToWayLineMode("pointToPoint")
                finishAction("noAction")
                exitOnRCLost("executeLostAction")
                executeRCLostAction("landing")
                takeOffSecurityHeight(1000)
                globalTransitionalSpeed(10.0)
                globalRTHHeight(100.0)
                droneInfo {
                    droneEnumValue(67)
                    droneSubEnumValue(0)
                }
                payloadInfo {
                    payloadEnumValue(52)
                    payloadSubEnumValue(0)
                    payloadPositionIndex(0)
                }
                folder {
                    templateId(1)
                    executeHeightMode("WGS84")
                    waylineId(1)
                    autoFlightSpeed(10.0)
                    placeMark {
                        point {
                            coordinates(30.0, 100.0)
                        }
                        index(1)
                        executeHeight(200.0)
                        waypointSpeed(10.0)
                        waypointHeadingParams {
                            waypointHeadingMode("followWayline")
                        }
                        waypointTurnParam {
                            waypointTurnMode("coordinateTurn")
                            waypointTurnDampingDist(10.0)
                        }
                        useStraightLine(true)
                        actionGroup {
                            actionGroupId(1)
                            actionGroupEndIndex(1)
                            actionGroupEndIndex(1)
                            actionGroupMode("sequence")
                            actionTrigger {
                                actionTriggerType("reachPoint")
                            }
                            for (i in 0 until 3) {
                                action {
                                    actionId(0)
                                    actionActuatorFunc("gimbalRotate")
                                    actionActuatorFuncParam {
                                        gimbalRotateMode("absoluteAngle")
                                        gimbalPitchRotateEnable(false)
                                        gimbalPitchRotateAngle(0.0)
                                        gimbalRollRotateEnable(false)
                                        gimbalRollRotateAngle(0.0)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.also {
            Log.i(TAG, "xml: ${it}")
        }
    }

    companion object {
        val TAG = MainActivity::class.simpleName
    }
}