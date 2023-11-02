package com.merpyzf.lib.wpml

import org.dom4j.Document
import org.dom4j.DocumentHelper
import org.dom4j.Element

/**
 *@author: WangKe
 *@date: 2023/10/17 0017
 */
class WPML {
    class Waypoint() {
        private val document: Document by lazy {
            DocumentHelper.createDocument()
        }
        private val root: Element by lazy {
            document.addElement(TAG.KML.text, "http://www.opengis.net/kml/2.2").apply {
                addNamespace(TAG.WPML.text, "http://www.dji.com/wpmz/1.0.3")
            }
        }
        private val docElement: Element by lazy {
            root.addElement(TAG.DOCUMENT.text)
        }

        /**
         * 任务信息
         */
        fun missionConfig(block: MissionConfig.() -> Unit) {
            val missionConfig = MissionConfig()
            docElement.add(missionConfig.rootElement)
            block.invoke(missionConfig)
        }

        /**
         * 模板信息
         */
        fun folder(block: Folder.() -> Unit) {
            val folder = Folder()
            docElement.add(folder.rootElement)
            block.invoke(folder)
        }

        /**
         * 构建航线
         */
        fun make(block: Waypoint.() -> Unit): String {
            block.invoke(this)
            return document.asXML()
        }
    }

    class Template {
        private val document: Document by lazy {
            DocumentHelper.createDocument()
        }
        private val root: Element by lazy {
            document.addElement(TAG.KML.text, "http://www.opengis.net/kml/2.2").apply {
                addNamespace(TAG.WPML.text, "http://www.dji.com/wpmz/1.0.3")
            }
        }

        private val docElement: Element by lazy {
            root.addElement(TAG.DOCUMENT.text)
        }

        /**
         * 作者
         */
        fun author(author: String) {
            docElement.addElement(TAG.AUTHOR.text).addText(author)
        }

        /**
         * 创建时间
         */
        fun createTime(timeMillis: Long) {
            docElement.addElement(TAG.CREATE_TIME.text).addText("$timeMillis")
        }

        /**
         * 更新时间
         */
        fun updateTime(timeMillis: Long) {
            docElement.addElement(TAG.UPDATE_TIME.text).addText("$timeMillis")
        }

        /**
         * 任务信息
         */
        fun missionConfig(block: MissionConfig.() -> Unit) {
            val missionConfig = MissionConfig()
            docElement.add(missionConfig.rootElement)
            block.invoke(missionConfig)
        }

        /**
         * 模板信息
         */
        fun folder(block: Folder.() -> Unit) {
            val folder = Folder()
            docElement.add(folder.rootElement)
            block.invoke(folder)
        }

        /**
         * 生成航线
         */
        fun make(block: Template.() -> Unit): String {
            block.invoke(this)
            return document.asXML()
        }
    }

    @ScopeMarker
    open class MissionConfig {
        val rootElement: Element = DocumentHelper.createElement(TAG.MISSION_CONFIG.text)

        /**
         * 飞向首航点模式（必须元素）
         * 说明：
         *      safely：安全模式
         *      （M300）飞行器起飞，上升至首航点高度，再平飞至首航点。如果首航点低于起飞点，则起飞后平飞至首航点上方再下降。
         *      （M30）飞行器起飞，上升至首航点高度，再平飞至首航点。如果首航点低于“安全起飞高度”，则起飞至“安全起飞高度”后，平飞至首航点上方再下降。注意“安全起飞高度”仅在飞行器未起飞时生效
         *      pointToPoint：倾斜飞行模式
         *      （M300）飞行器起飞后，倾斜飞到首航点。
         *      （M30）飞行器起飞至“安全起飞高度”，再倾斜爬升至首航点。如果首航点高度低于“安全起飞高度”，则先平飞后下降。
         *      支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun flyToWayLineMode(mode: FlyToWayLineMode) {
            rootElement.addElement(TAG.FLY_TO_WAY_LINE_MODE.text).addText(mode.text)
        }

        /**
         * 航线结束动作（必需元素）
         * 说明：
         *      goHome：飞行器完成航线任务后，退出航线模式并返航；
         *      noAction：飞行器完成航线任务后，退出航线模式；
         *      autoLand：飞行器完成航线任务后，退出航线模式并原地降落；
         *      gotoFirstWaypoint：飞行器完成航线任务后，立即飞向航线起始点，到达后退出航线模式。* 注：以上动作执行过程，若飞行器退出了航线模式且进入失控状态，则会优先执行失控动作。
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun finishAction(action: FinishAction) {
            rootElement.addElement(TAG.FINISH_ACTION.text).addText(action.text)
        }

        /**
         * 失控是否继续执行航线（必需元素）
         * 说明：
         *      goContinue：继续执行航线；
         *      executeLostAction：退出航线，执行失控动作
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun exitOnRCLost(action: String) {
            rootElement.addElement(TAG.EXIT_ON_RC_LOST.text).addText(action)
        }

        /**
         * 失控动作类型（当 wpml:exitOnRCLost 为 executeLostAction 时为必需元素）
         * 说明：
         *      goBack：返航。飞行器从失控位置飞向起飞点；
         *      landing：降落。飞行器从失控位置原地降落；
         *      hover：悬停。飞行器从失控位置悬停
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun executeRCLostAction(action: String) {
            rootElement.addElement(TAG.EXECUTE_RC_LOST_ACTION.text).addText(action)
        }

        /**
         * 安全起飞高度 m [1.5, 1500] （高度模式：相对起飞点高度）
         * 说明：飞行器起飞后，先爬升至该高度，再根据“飞向首航点模式”的设置飞至首航点。该元素仅在飞行器未起飞时生效。
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun takeOffSecurityHeight(height: Int) {
            rootElement.addElement(TAG.TAKE_OFF_SECURITY_HEIGHT.text).addText("$height")
        }

        /**
         * 参考起飞点 [-90,90],[-180,180],无限制（非必须元素）
         * 说明：参考起飞点”仅做航线规划参考，飞行器执行航线时以飞行器真实的起飞点为准，高度使用椭球高。
         * 支持机型：M30/M30T，M3E/M3T/M3M
         */
        fun takeOffRefPoint(x: Double, y: Double, z: Double) {
            rootElement.addElement(TAG.TAKE_OFF_REF_POINT.text).addText("${x},${y},${z}")
        }

        /**
         * 参考起飞点海拔高度 m（非必需元素）
         * 说明：”参考起飞点“海拔高度，与“参考起飞点”中的椭球高度对应。
         * 支持机型：M30/M30T，M3E/M3T/M3M
         */
        fun takeOffRefPointAGLHeight(height: Double) {
            rootElement.addElement(TAG.TAKE_OFF_REF_POINT_AGL_HEIGHT.text)
                .addText("$height")
        }

        /**
         * 全局航线过渡速度 [1,15]（必须元素）
         * 说明：飞行器飞往每条航线首航点的速度。航线任务中断时，飞行器从当前位置恢复至断点的速度。
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun globalTransitionalSpeed(speed: Double) {
            rootElement.addElement(TAG.GLOBAL_TRANSITIONAL_SPEED.text).addText("$speed")
        }

        /**
         * 全局返航高度（必需元素）
         * 说明：飞行器返航时，先爬升至该高度，再进行返航
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun globalRTHHeight(height: Double) {
            rootElement.addElement(TAG.GLOBAL_RTH_HEIGHT.text).addText("$height")
        }

        /**
         * 飞行器机型信息
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun droneInfo(block: DroneInfo.() -> Unit) {
            val droneInfo = DroneInfo()
            rootElement.add(droneInfo.rootElement)
            block.invoke(droneInfo)
        }

        /**
         * 负载机型信息
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun payloadInfo(block: PayloadInfo.() -> Unit) {
            val payloadInfo = PayloadInfo()
            rootElement.add(payloadInfo.rootElement)
            block.invoke(payloadInfo)
        }

        @ScopeMarker
        class DroneInfo {
            val rootElement: Element = DocumentHelper.createElement(TAG.DRONE_INFO.text)

            /**
             * 飞行器机型主类型（必需元素）
             * 说明：
             *      89（机型：M350 RTK）,
             *      60（机型：M300 RTK）,
             *      67（机型：M30/M30T）,
             *      77（机型：M3E/M3T/M3M）
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun droneEnumValue(value: Int) {
                rootElement.addElement(TAG.DRONE_ENUM_VALUE.text).addText("$value")
            }

            /**
             * 飞行器机型子类型（必要元素，当“飞行器机型主类型”为有效值时，该元素才是必需。）
             * 说明：
             *      当“飞行器机型主类型”为“67（机型：M30/M30T）”时：
             *      0（机型：M30双光）,
             *      1（机型：M30T三光）
             *      当“飞行器机型主类型“为”77（机型：M3E/M3T/M3M）“时：
             *      0（机型：M3E）
             *      1（机型：M3T）
             *      2（机型：M3M）
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun droneSubEnumValue(value: Int) {
                rootElement.addElement(TAG.DRONE_SUB_ENUM_VALUE.text).addText("$value")
            }
        }

        @ScopeMarker
        class PayloadInfo {
            val rootElement: Element = DocumentHelper.createElement(TAG.PAYLOAD_INFO.text)

            /**
             * 负载机型主类型（必需元素）
             * 说明：
             *      42（H20）,
             *      43（H20T）,
             *      52（M30双光相机）,
             *      53（M30T三光相机）,
             *      61（H20N）,
             *      66（Mavic 3E 相机）
             *      67（Mavic 3T 相机）
             *      68（Mavic 3M 相机）
             *      65534（PSDK 负载）
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun payloadEnumValue(value: Int) {
                rootElement.addElement(TAG.PAY_LOAD_ENUM_VALUE.text).addText("$value")
            }


            fun payloadSubEnumValue(value: Int) {
                rootElement.addElement(TAG.PAY_LOAD_SUB_ENUM_VALUE.text).addText("$value")
            }

            /**
             * 负载挂载位置（必需元素）
             * 说明：
             *      0：飞行器1号挂载位置。M300 RTK，M350 RTK机型，对应机身左前方。其它机型，对应主云台。
             *      1：飞行器2号挂载位置。M300 RTK，M350 RTK机型，对应机身右前方。
             *      2：飞行器3号挂载位置。M300 RTK，M350 RTK机型，对应机身上方。
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun payloadPositionIndex(value: Int) {
                rootElement.addElement(TAG.PAY_LOAD_POSITION_INDEX.text).addText("$value")
            }
        }
    }

    @ScopeMarker
    class Folder {
        val rootElement: Element = DocumentHelper.createElement(TAG.Folder.text)

        /**
         * 预定义模板类型
         * 说明：模板为用户提供了快速生成航线的方案。用户填充模板元素，再导入大疆支持客户端（如DJI Pilot），即可快速生成可执行的测绘/巡检航线。
         *      waypoint：航点飞行
         *      mapping2d：建图航拍
         *      mapping3d：倾斜摄影
         *      mappingStrip：航带飞行
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun templateType(type: String) {
            rootElement.addElement(TAG.TEMPLATE_TYPE.text).addText(type)
        }

        /**
         * 全局航线过渡速度(必须元素)[1,15]
         * 说明：飞行器飞往每条航线首航点的速度。航线任务中断时，飞行器从当前位置恢复至断点的速度。
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun useGlobalTransitionalSpeed(speed: Double) {
            rootElement.addElement(TAG.USE_GLOBAL_TRANSITIONAL_SPEED.text).addText("${speed}")
        }

        /**
         * 模板ID（必需元素）
         * 说明：在一个kmz文件内该ID唯一。建议从0开始单调连续递增。在template.kml和waylines.wpml文件中，将使用该id将模板与所生成的可执行航线进行关联。
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun templateId(id: Int) {
            rootElement.addElement(TAG.TEMPLATE_ID.text).addText("$id")
        }

        /**
         * 执行高度模式，该元素仅在 waylines.wpml 中使用。（必需元素）
         * 说明：
         *      WGS84：椭球高模式
         *      relativeToStartPoint：相对起飞点高度模式
         *      realTimeFollowSurface: 使用实时仿地模式，仅支持M3E/M3T/M3M
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun executeHeightMode(mode: String) {
            rootElement.addElement(TAG.EXECUTE_HEIGHT_MODE.text).addText(mode)
        }

        /**
         * 航线ID（必需元素）
         * 说明：在一条航线中该ID唯一。建议从0开始单调连续递增。
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun waylineId(id: Int) {
            rootElement.addElement(TAG.WAYLINE_ID.text).addText("$id")
        }

        fun wayLineCoordinateSysParams(block: WayLineCoordinateSysParam.() -> Unit) {
            val obj = WayLineCoordinateSysParam()
            rootElement.add(obj.rootElement)
            block.invoke(obj)
        }

        /**
         * 全局航线飞行速度 [1,15] （必需元素）
         * 说明：该元素定义了此模板生成的整段航线中，飞行器的目标飞行速度。如果额外定义了某航点的该元素，则局部定义会覆盖全局定义。
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun autoFlightSpeed(speed: Double) {
            rootElement.addElement(TAG.AUTO_FLIGHT_SPEED.text).addText("$speed")
        }

        /**
         * 全局航线过渡速度 [1,15] （必需元素）
         * 说明：飞行器飞往每条航线首航点的速度。航线任务中断时，飞行器从当前位置恢复至断点的速度。
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun transitionalSpeed(speed: Double) {
            rootElement.addElement(TAG.TRANSITIONAL_SPEED.text).addText("$speed")
        }

        /**
         * 云台俯仰角控制模式（必需元素）
         * 说明：
         *      manual：手动控制。飞行器从一个航点飞向下一个航点的过程中，支持用户手动控制云台的俯仰角度。若无用户控制，则保持飞离航点时的云台俯仰角度。
         *      usePointSetting：依照每个航点设置。飞行器从一个航点飞向下一个航点的过程中，云台俯仰角均匀过渡至下一个航点的俯仰角。
         * 支持型号：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun gimbalPitchMode(mode: String) {
            rootElement.addElement(TAG.GIMBAL_PITCH_MODE.text).addText(mode)
        }

        fun globalWaypointHeadingParam(block: GlobalWaypointHeadingParam.() -> Unit) {
            val globalWaypointHeadingParam = GlobalWaypointHeadingParam()
            rootElement.add(globalWaypointHeadingParam.rootElement)
            block.invoke(globalWaypointHeadingParam)
        }

        /**
         * 全局航点类型（全局航点转弯模式）（必需元素）
         * 说明：
         *      coordinateTurn：协调转弯，不过点，提前转弯
         *      toPointAndStopWithDiscontinuityCurvature：直线飞行，飞行器到点停
         *      toPointAndStopWithContinuityCurvature：曲线飞行，飞行器到点停
         *      toPointAndPassWithContinuityCurvature：曲线飞行，飞行器过点不停
         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
         */
        fun globalWaypointTurnMode(mode: String) {
            rootElement.addElement(TAG.GLOBAL_WAYPOINT_TURN_MODE.text).addText(mode)
        }

        /**
         * 全局航段轨迹是否尽量贴合直线（必须元素）
         * 说明：当且仅当“wpml:globalWaypointTurnMode”被设置为“toPointAndStopWithContinuityCurvature”或“toPointAndPassWithContinuityCurvature”时必需。如果额外定义了某航点的该元素，则局部定义会覆盖全局定义。
         *      0：航段轨迹全程为曲线
         *      1：航段轨迹尽量贴合两点连线
         * 支持机型：M30/M30T，M3E/M3T/M3M
         */
        fun globalUseStraightLine(isStraightLine: Boolean) {
            rootElement.addElement(TAG.GLOBAL_USE_STRAIGHT_LINE.text).addText(
                "${
                    if (isStraightLine) {
                        1
                    } else {
                        0
                    }
                }"
            )
        }

        /**
         * 航点信息
         */
        fun placeMark(block: PlaceMark.() -> Unit) {
            val placeMark = PlaceMark()
            rootElement.add(placeMark.rootElement)
            block.invoke(placeMark)
        }

        @ScopeMarker
        class PlaceMark {
            val rootElement: Element = DocumentHelper.createElement(TAG.PLACEMARK.text)
            fun point(block: Point.() -> Unit) {
                val point = Point()
                rootElement.add(point.rootElement)
                block.invoke(point)
            }

            /**
             * 航点序号 [0, 65535] （必需元素）
             * 说明：在一条航线内该ID唯一。该序号必须从0开始单调连续递增。
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun index(index: Int) {
                rootElement.addElement(TAG.INDEX.text).addText("$index")
            }

            /**
             * 航点执行高度 m （必需元素）
             * 说明：该元素仅在waylines.wpml中使用。具体高程参考平面在“wpml:executeHeightMode”中声明。
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun executeHeight(height: Double) {
                rootElement.addElement(TAG.EXECUTE_HEIGHT.text).addText("$height")
            }

            /**
             * 是否使用全局高度（必需元素）
             * 说明：
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun useGlobalHeight(isUse: Boolean) {
                rootElement.addElement(TAG.USE_GLOBAL_HEIGHT.text).addText(
                    "${
                        if (isUse) {
                            1
                        } else {
                            0
                        }
                    }"
                )
            }

            /**
             * 航点高度（WGS84椭球高度）（必需元素，* 注：当且仅当“wpml:useGlobalHeight”为“0”时必需）
             * 说明：该元素与“wpml:height”配合使用，二者是同一位置不同高程参考平面的表达。
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun ellipsoidHeight(height: Double) {
                rootElement.addElement(TAG.ELLIPSOID_HEIGHT.text).addText("$height")
            }

            /**
             * 航点高度（EGM96海拔高度/相对起飞点高度/AGL相对地面高度）
             * 说明：该元素与“wpml:ellipsoidHeight”配合使用，二者是同一位置不同高程参考平面的表达
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun height(height: Double) {
                rootElement.addElement(TAG.HEIGHT.text).addText("$height")
            }

            /**
             * 是否使用全局飞行速度
             * 说明：此处的全局飞行速度即“wpml:autoFlightSpeed”
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun useGlobalSpeed(isUse: Boolean) {
                rootElement.addElement(TAG.USE_GLOBAL_SPEED.text).addText(
                    "${
                        if (isUse) {
                            1
                        } else {
                            0
                        }
                    }"
                )
            }

            /**
             * 航点飞行速度 m/s [1,15] （必需元素，* 注：当且仅当“wpml:useGlobalSpeed”为“0”时必需）
             * 说明：
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun waypointSpeed(speed: Double) {
                rootElement.addElement(TAG.WAYPOINT_SPEED.text).addText("${speed}")
            }

            fun waypointHeadingParams(block: WayPointHeadingParam.() -> Unit) {
                val wayPointHeadingParam = WayPointHeadingParam()
                rootElement.add(wayPointHeadingParam.rootElement)
                block.invoke(wayPointHeadingParam)
            }

            /**
             * 航点转弯设置
             */
            fun waypointTurnParam(block: WaypointTurnParam.() -> Unit) {
                val waypointTurnParam = WaypointTurnParam()
                rootElement.add(waypointTurnParam.rootElement)
                block.invoke(waypointTurnParam)
            }

            /**
             * 该段航线是否贴合直线（必需元素，当且仅当“wpml:waypointTurnParam”内"waypointTurnMode"被设置为“toPointAndStopWithContinuityCurvature”或“toPointAndPassWithContinuityCurvature”时必需。如果此元素被设置，则局部定义会覆盖全局定义。）
             * 说明：
             *      0：航段轨迹全程为曲线
             *      1：航段轨迹尽量贴合两点连线
             * 支持机型：M30/M30T，M3E/M3T/M3M
             */
            fun useStraightLine(isStraightLine: Boolean) {
                rootElement.addElement(TAG.USE_STRAIGHT_LINE.text).addText(
                    "${
                        if (isStraightLine) {
                            1
                        } else {
                            0
                        }
                    }"
                )
            }

            /**
             * 是否使用全局偏航角模式参数（必要元素）
             * 说明：
             * 0：不使用全局设置
             * 1：使用全局设置
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun useGlobalHeadingParam(isUse: Boolean) {
                rootElement.addElement(TAG.USE_GLOBAL_HEADING_PARAM.text).addText(
                    "${
                        if (isUse) {
                            1
                        } else {
                            0
                        }
                    }"
                )
            }

            /**
             * 是否使用全局航点类型（全局航点转弯模式）（必需元素）
             * 说明：
             * 0：不使用全局设置
             * 1：使用全局设置
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun useGlobalTurnParam(isUse: Boolean) {
                rootElement.addElement(TAG.USE_GLOBAL_TURN_PARAM.text).addText(
                    "${
                        if (isUse) {
                            1
                        } else {
                            0
                        }
                    }"
                )
            }

            /**
             * 航点云台俯仰角（必需元素，* 注：当且仅当“wpml:gimbalPitchMode”为“usePointSetting”时必需。）
             * 说明：对应机型云台可转动范围
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun gimbalPitchAngle(angle: Double) {
                rootElement.addElement(TAG.GIMBAL_PITCH_ANGLE.text).addText("$angle")
            }

            /**
             * 动作组
             */
            fun actionGroup(block: ActionGroup.() -> Unit) {
                val actionGroup = ActionGroup()
                rootElement.add(actionGroup.rootElement)
                block.invoke(actionGroup)
            }

            @ScopeMarker
            class Point {
                val rootElement: Element = DocumentHelper.createElement(TAG.POINT.text)

                /**
                 * 航点经纬度 [-90,90],[-180,180]（必须元素）
                 * 说明：此处格式如“<Point> <coordinates> 经度,纬度 </coordinates> </Point>“
                 * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                 */
                fun coordinates(longitude: Double, latitude: Double) {
                    rootElement.addElement(TAG.COORDINATES.text).addText(
                        "$longitude,$latitude"
                    )
                }
            }

            @ScopeMarker
            class ActionGroup {
                val rootElement: Element = DocumentHelper.createElement(TAG.ACTION_GROUP.text)

                /**
                 * 动作组id [0, 65535] （必须元素）
                 * 说明：在一个kmz文件内该ID唯一。建议从0开始单调连续递增。
                 * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                 */
                fun actionGroupId(id: Int) {
                    rootElement.addElement(TAG.ACTION_GROUP_ID.text).addText("$id")
                }

                /**
                 * 动作组开始生效的航点 [0, 65535] （必须元素）
                 * 说明：
                 * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                 */
                fun actionGroupStartIndex(index: Int) {
                    rootElement.addElement(TAG.ACTION_GROUP_START_INDEX.text).addText("$index")
                }

                /**
                 * 动作组结束生效的航点 [0, 65535] （必需元素，该元素必须大于等于“actionGroupStartIndex）
                 * 说明：当“动作组结束生效的航点”与“动作组开始生效的航点”一致，则代表该动作组仅在该航点处生效。
                 * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                 */
                fun actionGroupEndIndex(index: Int) {
                    rootElement.addElement(TAG.ACTION_GROUP_END_INDEX.text).addText("$index")
                }

                /**
                 * 动作执行模式（必需元素）
                 * 说明：sequence：串行执行。即动作组内的动作依次按顺序执行。
                 * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                 */
                fun actionGroupMode(mode: String) {
                    rootElement.addElement(TAG.ACTION_GROUP_MODE.text).addText(mode)
                }

                /**
                 * 动作组触发器
                 */
                fun actionTrigger(block: ActionTrigger.() -> Unit) {
                    val actionTrigger = ActionTrigger()
                    rootElement.add(actionTrigger.rootElement)
                    block.invoke(actionTrigger)
                }

                fun action(block: Action.() -> Unit) {
                    val action = Action()
                    rootElement.add(action.rootElement)
                    block.invoke(action)
                }

                @ScopeMarker
                class ActionTrigger {
                    val rootElement: Element =
                        DocumentHelper.createElement(TAG.ACTION_TRIGGER.text)

                    /**
                     * 动作触发器类型（必需元素）
                     * 说明：
                     *      reachPoint：到达航点时执行
                     *      betweenAdjacentPoints：航段触发，均匀转云台
                     *      multipleTiming：等时触发
                     *      multipleDistance：等距触发
                     *      * 注：“betweenAdjacentPoints”需配合动作"gimbalEvenlyRotate"使用
                     * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                     */
                    fun actionTriggerType(type: String) {
                        rootElement.addElement(TAG.ACTION_TRIGGER_TYPE.text).addText(type)
                    }

                    /**
                     * 动作触发器参数 > 0
                     * 说明：当“actionTriggerType”为“multipleTiming”时，该元素表示间隔时间，单位是s。当“actionTriggerType”为“multipleDistance”时，该元素表示间隔距离，单位是m。
                     * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                     */
                    fun actionTriggerParam(param: Double) {
                        rootElement.addElement(TAG.ACTION_TRIGGER_PARAM.text).addText("$param")
                    }
                }

                @ScopeMarker
                class Action {
                    val rootElement: Element = DocumentHelper.createElement(TAG.ACTION.text)

                    /**
                     * 动作id [0, 65535] （必需元素）
                     * 说明：在一个动作组内该ID唯一。建议从0开始单调连续递增。
                     * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                     */
                    fun actionId(id: Int) {
                        rootElement.addElement(TAG.ACTION_ID.text).addText("$id")
                    }

                    /**
                     * 动作类型（必需元素）
                     * 说明：
                     *      takePhoto：单拍
                     *      startRecord：开始录像
                     *      stopRecord：结束录像
                     *      focus：对焦
                     *      zoom：变焦
                     *      customDirName：创建新文件夹
                     *      gimbalRotate：旋转云台
                     *      rotateYaw：飞行器偏航
                     *      hover：悬停等待
                     *      gimbalEvenlyRotate：航段间均匀转动云台pitch角
                     *      accurateShoot：精准复拍动作（已暂停维护，建议使用orientedShoot）
                     *      orientedShoot：精准复拍动作
                     * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                     */
                    fun actionActuatorFunc(func: String) {
                        rootElement.addElement(TAG.ACTION_ACTUATOR_FUNC.text).addText(func)
                    }

                    fun actionActuatorFuncParam(block: ActionActuatorFuncParam.() -> Unit) {
                        val actionActuatorFuncParam = ActionActuatorFuncParam()
                        rootElement.add(actionActuatorFuncParam.rootElement)
                        block.invoke(actionActuatorFuncParam)
                    }

                    @ScopeMarker
                    class ActionActuatorFuncParam {
                        val rootElement: Element =
                            DocumentHelper.createElement(TAG.ACTION_ACTUATOR_FUNC_PARAM.text)

                        /**
                         * 云台转动模式（必需元素）
                         * 说明：
                         *      relativeAngle：相对角度，相对于飞行器机头的角度
                         *      absoluteAngle：绝对角度，相对于正北方的角度
                         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                         */
                        fun gimbalRotateMode(mode: String) {
                            rootElement.addElement(TAG.GIMBAL_ROTATE_MODE.text).addText(mode)
                        }

                        /**
                         * 是否使能云台Pitch转动（必须元素）
                         * 说明：
                         *      0：不使能
                         *      1：使能
                         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                         */
                        fun gimbalPitchRotateEnable(isEnable: Boolean) {
                            rootElement.addElement(TAG.GIMBAL_PITCH_ROTATE_ENABLE.text).addText(
                                "${
                                    if (isEnable) {
                                        1
                                    } else {
                                        0
                                    }
                                }"
                            )
                        }

                        /**
                         * 云台Pitch转动角度（必须元素）
                         * 说明：不同云台可转动范围不同
                         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                         */
                        fun gimbalPitchRotateAngle(angle: Double) {
                            rootElement.addElement(TAG.GIMBAL_PITCH_ROTATE_ANGLE.text)
                                .addText("${angle}")
                        }

                        /**
                         * 是否使能云台Roll转动（必需元素）
                         * 说明：
                         *      0：不使能
                         *      1：使能
                         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                         */
                        fun gimbalRollRotateEnable(isEnable: Boolean) {
                            rootElement.addElement(TAG.GIMBAL_ROLL_ROTATE_ENABLE.text).addText(
                                "${
                                    if (isEnable) {
                                        1
                                    } else {
                                        0
                                    }
                                }"
                            )
                        }

                        /**
                         * 云台Roll转动角度（必须元素）
                         * 说明：不同云台可转动范围不同
                         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                         */
                        fun gimbalRollRotateAngle(angle: Double) {
                            rootElement.addElement(TAG.GIMBAL_ROLL_ROTATE_ANGLE.text)
                                .addText("${angle}")
                        }

                        /**
                         * 是否使能云台Yaw转动（必需元素）
                         * 说明：
                         *      0：不使能
                         *      1：使能
                         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                         */
                        fun gimbalYawRotateEnable(isEnable: Boolean) {
                            rootElement.addElement(TAG.GIMBAL_YAW_ROTATE_ENABLE.text).addText(
                                "${
                                    if (isEnable) {
                                        1
                                    } else {
                                        0
                                    }
                                }"
                            )
                        }

                        /**
                         * 云台Yaw转动角度（必须元素）
                         * 说明：不同云台可转动范围不同
                         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                         */
                        fun gimbalYawRotateAngle(angle: Double) {
                            rootElement.addElement(TAG.GIMBAL_YAW_ROTATE_ANGLE.text)
                                .addText("$angle")
                        }

                        /**
                         * 是否使能云台转动时间（必需元素）
                         * 说明：
                         *      0：不使能
                         *      1：使能
                         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                         */
                        fun gimbalRotateTimeEnable(isEnable: Boolean) {
                            rootElement.addElement(TAG.GIMBAL_ROTATE_TIME_ENABLE.text).addText(
                                "${
                                    if (isEnable) {
                                        1
                                    } else {
                                        0
                                    }
                                }"
                            )
                        }

                        /**
                         * 云台完成转动用时 s
                         * 说明：
                         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                         */
                        fun gimbalRotateTime(time: Double) {
                            rootElement.addElement(TAG.GIMBAL_ROTATE_TIME.text).addText("$time")
                        }

                        /**
                         * 负载挂载位置（必需元素）
                         * 说明：
                         *      0：飞行器1号挂载位置。M300 RTK，M350 RTK机型，对应机身左前方。其它机型，对应主云台。
                         *      1：飞行器2号挂载位置。M300 RTK，M350 RTK机型，对应机身右前方。
                         *      2：飞行器3号挂载位置。M300 RTK，M350 RTK机型，对应机身上方。
                         * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
                         */
                        fun payloadPositionIndex(index: Int) {
                            rootElement.addElement(TAG.PAYLOAD_POSITION_INDEX.text)
                                .addText("$index")
                        }

                        /**
                         * 拍摄照片文件后缀（必需元素）
                         * 说明：为生成媒体文件命名时将额外附带该后缀。
                         * 支持机型：M300 RTK和M350 RTK（负载H20/H20T/H20N），M30/M30T，M3E/M3T/M3M
                         */
                        fun fileSuffix(suffix: String) {
                            rootElement.addElement(TAG.FILE_SUFFIX.text).addText("$suffix")
                        }
                    }
                }
            }
        }

        @ScopeMarker
        open class GlobalWaypointHeadingParam {
            var rootElement: Element =
                DocumentHelper.createElement(TAG.GLOBAL_WAYPOINT_HEADING_PARAM.text)

            /**
             * 飞行器偏航角模式（必需元素）
             * 说明：
             *      followWayline：沿航线方向。飞行器机头沿着航线方向飞至下一航点
             *      manually：手动控制。飞行器在飞至下一航点的过程中，用户可以手动控制飞行器机头朝向
             *      fixed：锁定当前偏航角。飞行器机头保持执行完航点动作后的飞行器偏航角飞至下一航点
             *      smoothTransition：自定义。通过“wpml:waypointHeadingAngle”给定某航点的目标偏航角，并在航段飞行过程中均匀过渡至下一航点的目标偏航角。
             *      towardPOI：朝向兴趣点
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun waypointHeadingMode(mode: String) {
                rootElement.addElement(TAG.WAY_POINT_HEADING_MODE.text).addText(mode)
            }

            /**
             * 飞行器偏航角度（当且仅当“wpml:waypointHeadingMode”为“smoothTransition”时必需）
             * 说明：给定某航点的目标偏航角，并在航段飞行过程中均匀过渡至下一航点的目标偏航角。
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun waypointHeadingAngle(angle: Int) {
                rootElement.addElement(TAG.WAYPOINT_HEADING_ANGLE.text).addText("$angle")
            }

            /**
             * 数据格式为：纬度,经度,高度（仅当wpml:waypointHeadingMode为towardPOI时必需）
             * 说明：仅当wpml:waypointHeadingMode为towardPOI该字段生效。目前不支持Z方向朝向兴趣点，高度可设置为0。当某一航点wpml:waypointHeadingMode设置为towardPOI后，飞行器从该航点飞向下一航点途中机头都将朝向兴趣点
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun waypointPoiPoint(x: Double, y: Double, z: Double) {
                rootElement.addElement(TAG.WAYPOINT_POI_POINT.text).addText("$x,$y,$z")
            }

            /**
             * 飞行器偏航角转动方向（必需元素）
             * 说明：
             *      clockwise：顺时针旋转飞行器偏航角
             *      counterClockwise：逆时针旋转飞行器偏航角
             *      followBadArc：沿最短路径旋转飞行器偏航角
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun waypointHeadingPathMode(mode: String) {
                rootElement.addElement(TAG.WAYPOINT_HEADING_PATH_MODE.text).addText(mode)
            }
        }

        @ScopeMarker
        class WayPointHeadingParam : GlobalWaypointHeadingParam() {
            init {
                rootElement = DocumentHelper.createElement(TAG.WAYPOINT_HEADING_PARAM.text)
            }
        }

        @ScopeMarker
        class WaypointTurnParam {
            val rootElement: Element = DocumentHelper.createElement(TAG.WAYPOINT_TURN_PARAM.text)

            /**
             * 航点类型（航点转弯模式）（必须元素）
             * 说明：
             *      coordinateTurn：协调转弯，不过点，提前转弯
             *      toPointAndStopWithDiscontinuityCurvature：直线飞行，飞行器到点停
             *      toPointAndStopWithContinuityCurvature：曲线飞行，飞行器到点停
             *      toPointAndPassWithContinuityCurvature：曲线飞行，飞行器过点不停
             *      注：DJI Pilot 2/司空 2 上“平滑过点，提前转弯”模式设置方法为：
             *      1）将wpml:waypointTurnMode设置为toPointAndPassWithContinuityCurvature
             *      2）将wpml:useStraightLine设置为1
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun waypointTurnMode(mode: String) {
                rootElement.addElement(TAG.WAYPOINT_TURN_MODE.text).addText(mode)
            }

            /**
             * 航点转弯截距 (0, 航段最大长度] m （必须元素* 注：当且仅当以下两种情况下必需“wpml:waypointTurnMode”为“coordinateTurn”“wpml:waypointTurnMode”为“toPointAndPassWithContinuityCurvature”，且“wpml:useStraightLine”为“1”）
             * 说明：两航点间航段长度必需大于两航点转弯截距之和。此元素定义了飞行器在距离该航点若干米前，提前多少距离转弯。
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun waypointTurnDampingDist(dist: Double) {
                rootElement.addElement(TAG.WAYPOINT_TURN_DAMPING_DIST.text).addText("$dist")
            }
        }

        @ScopeMarker
        class WayLineCoordinateSysParam {
            var rootElement: Element =
                DocumentHelper.createElement(TAG.WAYLINE_COORDINATE_SYS_PARAM.text)

            /**
             * 经纬度坐标系（必需元素）
             * 说明：WGS84：当前固定使用 WGS84坐标系
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun coordinateMode(mode: String) {
                rootElement.addElement(TAG.COORDINATE_MODE.text).addText(mode)
            }

            /**
             * 航点高程参考平面（必需元素）
             * 说明：
             *      EGM96：使用海拔高编辑
             *      relativeToStartPoint：使用相对点的高度进行编辑
             *      aboveGroundLevel：使用地形数据，AGL下编辑(仅支持司空2平台)
             *      realTimeFollowSurface: 使用实时仿地模式（仅用于建图航拍模版），仅支持M3E/M3T/M3M机型
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun heightMode(mode: String) {
                rootElement.addElement(TAG.HEIGHT_MODE.text).addText(mode)
            }

            /**
             * 经纬度与高度数据源（非必需元素，该元素仅用于标记位置数据来源，不影响实际航线执行。）
             * 说明：
             *      GPS：位置数据采集来源为GPS/BDS/GLONASS/GALILEO等
             *      RTKBaseStation：采集位置数据时，使用RTK基站进行差分定位
             *      QianXun：采集位置数据时，使用千寻网络RTK进行差分定位
             *      Custom：采集位置数据时，使用自定义网络RTK进行差分定位
             * 支持设备：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun positioningType(type: String) {
                rootElement.addElement(TAG.POSITIONING_TYPE.text).addText(type)
            }

            /**
             * 飞行器离被摄面高度（相对地面高）（必需元素）
             * 说明：仅适用于模板类型mapping2d，mapping3d，mappingStrip
             * 支持设备：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun globalShootHeight(height: Double) {
                rootElement.addElement(TAG.GLOBAL_SHOOT_HEIGHT.text).addText("$height")
            }

            /**
             * 是否开启仿地飞行（必需元素）
             * 说明：仅适用于模板类型mapping2d，mapping3d，mappingStrip
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun surfaceFollowModeEnable(isEnable: Boolean) {
                rootElement.addElement(TAG.SURFACE_FOLLOW_MODE_ENABLE.text).addText(
                    "${
                        if (isEnable) {
                            1
                        } else {
                            0
                        }
                    }"
                )
            }

            /**
             * 仿地飞行离地高度（相对地面高）（必须元素，当且仅当“wpml:surfaceFollowModeEnable”为“1”时必需）
             * 说明：仅适用于模板类型mapping2d，mapping3d，mappingStrip
             * 支持机型：M300 RTK，M350 RTK，M30/M30T，M3E/M3T/M3M
             */
            fun surfaceRelativeHeight(height: Double) {
                rootElement.addElement(TAG.SURFACE_RELATIVE_HEIGHT.text).addText("$height")
            }
        }
    }

    enum class TAG(val text: String) {
        KML("kml"),
        WPML("wpml"),
        DOCUMENT("Document"),
        AUTHOR("wpml:author"),
        CREATE_TIME("wpml:createTime"),
        UPDATE_TIME("wpml:updateTime"),

        MISSION_CONFIG("wpml:missionConfig"),
        FLY_TO_WAY_LINE_MODE("wpml:flyToWaylineMode"),
        FINISH_ACTION("wpml:finishAction"),
        EXIT_ON_RC_LOST("wpml:exitOnRCLost"),
        EXECUTE_RC_LOST_ACTION("wpml:executeRCLostAction"),
        TAKE_OFF_SECURITY_HEIGHT("wpml:takeOffSecurityHeight"),
        TAKE_OFF_REF_POINT("wpml:takeOffRefPoint"),
        TAKE_OFF_REF_POINT_AGL_HEIGHT("wpml:takeOffRefPointAGLHeight"),
        GLOBAL_TRANSITIONAL_SPEED("wpml:globalTransitionalSpeed"),
        GLOBAL_RTH_HEIGHT("wpml:globalRTHHeight"),
        DRONE_INFO("wpml:droneInfo"),
        DRONE_ENUM_VALUE("wpml:droneEnumValue"),
        DRONE_SUB_ENUM_VALUE("wpml:droneSubEnumValue"),
        PAYLOAD_INFO("wpml:payloadInfo"),
        PAY_LOAD_ENUM_VALUE("wpml:payloadenumvalue"),
        PAY_LOAD_SUB_ENUM_VALUE("wpml:payloadSubEnumValue"),
        PAY_LOAD_POSITION_INDEX("wpml:payloadPositionIndex"),

        Folder("Folder"),
        TEMPLATE_TYPE("wpml:templateType"),
        USE_GLOBAL_TRANSITIONAL_SPEED("wpml:useGlobalTransitionalSpeed"),
        TEMPLATE_ID("wpml:templateId"),
        WAYLINE_COORDINATE_SYS_PARAM("wpml:waylineCoordinateSysParam"),
        COORDINATE_MODE("wpml:coordinateMode"),
        HEIGHT_MODE("wpml:heightMode"),
        GLOBAL_SHOOT_HEIGHT("wpml:globalShootHeight"),
        POSITIONING_TYPE("wpml:positioningType"),
        SURFACE_FOLLOW_MODE_ENABLE("wpml:surfaceFollowModeEnable"),
        SURFACE_RELATIVE_HEIGHT("wpml:surfaceRelativeHeight"),
        AUTO_FLIGHT_SPEED("wpml:autoFlightSpeed"),
        TRANSITIONAL_SPEED("wpml:transitionalSpeed"),
        GIMBAL_PITCH_MODE("wpml:gimbalPitchMode"),
        GLOBAL_WAYPOINT_HEADING_PARAM("wpml:globalWaypointHeadingParam"),
        WAYPOINT_HEADING_PARAM("wpml:waypointHeadingParam"),
        WAYPOINT_TURN_PARAM("wpml:waypointTurnParam"),
        WAYPOINT_TURN_MODE("wpml:waypointTurnMode"),
        WAYPOINT_TURN_DAMPING_DIST("wpml:waypointTurnDampingDist"),
        WAY_POINT_HEADING_MODE("wpml:waypointHeadingMode"),
        WAYPOINT_HEADING_ANGLE("wpml:waypointHeadingAngle"),
        WAYPOINT_POI_POINT("wpml:waypointPoiPoint"),
        WAYPOINT_HEADING_PATH_MODE("wpml:waypointHeadingPathMode"),
        GLOBAL_WAYPOINT_TURN_MODE("wpml:globalWaypointTurnMode"),
        GLOBAL_USE_STRAIGHT_LINE("wpml:globalUseStraightLine"),
        USE_STRAIGHT_LINE("wpml:useStraightLine"),
        PLACEMARK("Placemark"),
        POINT("Point"),
        COORDINATES("wpml:coordinates"),
        INDEX("wpml:index"),
        ELLIPSOID_HEIGHT("wpml:ellipsoidHeight"),
        HEIGHT("wpml:height"),
        USE_GLOBAL_HEIGHT("wpml:useGlobalHeight"),
        USE_GLOBAL_SPEED("wpml:useGlobalSpeed"),
        WAYPOINT_SPEED("wpml:waypointSpeed"),
        USE_GLOBAL_HEADING_PARAM("wpml:useGlobalHeadingParam"),
        USE_GLOBAL_TURN_PARAM("wpml:useGlobalTurnParam"),
        GIMBAL_PITCH_ANGLE("wpml:gimbalPitchAngle"),
        ACTION_GROUP("wpml:actionGroup"),
        ACTION_GROUP_ID("wpml:actionGroupId"),
        ACTION_GROUP_START_INDEX("wpml:actionGroupStartIndex"),
        ACTION_GROUP_END_INDEX("wpml:actionGroupEndIndex"),
        ACTION_GROUP_MODE("wpml:actionGroupMode"),
        ACTION_TRIGGER("wpml:actionTrigger"),
        ACTION_TRIGGER_TYPE("wpml:actionTriggerType"),
        ACTION_TRIGGER_PARAM("wpml:actionTriggerParam"),
        ACTION("wpml:action"),
        ACTION_ID("wpml:actionId"),
        ACTION_ACTUATOR_FUNC("wpml:actionActuatorFunc"),
        ACTION_ACTUATOR_FUNC_PARAM("wpml:actionActuatorFuncParam"),
        GIMBAL_ROTATE_MODE("wpml:gimbalRotateMode"),
        GIMBAL_PITCH_ROTATE_ENABLE("wpml:gimbalPitchRotateEnable"),
        GIMBAL_PITCH_ROTATE_ANGLE("wpml:gimbalPitchRotateAngle"),
        GIMBAL_ROLL_ROTATE_ENABLE("wpml:gimbalRollRotateEnable"),
        GIMBAL_ROLL_ROTATE_ANGLE("wpml:gimbalRollRotateAngle"),
        GIMBAL_YAW_ROTATE_ENABLE("wpml:gimbalYawRotateEnable"),
        GIMBAL_YAW_ROTATE_ANGLE("wpml:gimbalYawRotateAngle"),
        GIMBAL_ROTATE_TIME_ENABLE("wpml:gimbalRotateTimeEnable"),
        GIMBAL_ROTATE_TIME("wpml:gimbalRotateTime"),
        PAYLOAD_POSITION_INDEX("wpml:payloadPositionIndex"),
        FILE_SUFFIX("wpml:fileSuffix"),

        EXECUTE_HEIGHT_MODE("wpml:executeHeightMode"),
        WAYLINE_ID("wpml:waylineId"),
        EXECUTE_HEIGHT("wpml:executeHeight"),
    }
}