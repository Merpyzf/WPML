package com.merpyzf.lib.wpml

enum class FlyToWayLineMode(val text: String) {
    SAFELY("safely"),
    POINT_TO_POINT("pointToPoint")
}

enum class FinishAction(val text: String) {
    /**
     * 飞行器完成航线任务后，退出航线模式并返航
     */
    GO_HOME("goHome"),

    /**
     * 飞行器完成航线任务后，退出航线模式
     */
    NO_ACTION("noAction"),

    /**
     * 飞行器完成航线任务后，退出航线模式并原地降落
     */
    AUTO_LAND("autoLand"),

    /**
     * 飞行器完成航线任务后，立即飞向航线起始点，到达后退出航线模式
     */
    GOTO_FIRST_WAYPOINT("gotoFirstWaypoint")

}