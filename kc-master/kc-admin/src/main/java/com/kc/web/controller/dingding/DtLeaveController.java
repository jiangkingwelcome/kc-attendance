package com.kc.web.controller.dingding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kc.common.core.controller.BaseController;
import com.kc.common.core.domain.AjaxResult;
import com.kc.dingtalk.service.IDtLeaveService;

/**
 * 钉钉请假Controller
 *
 * @author kc
 * @date 2026-01-05
 */
@RestController
@RequestMapping("/dingtalk/leave")
public class DtLeaveController extends BaseController
{
    @Autowired
    private IDtLeaveService dtLeaveService;

    /**
     * 查询今日请假人数
     */
    @GetMapping("/todayCount")
    public AjaxResult getTodayLeaveCount()
    {
        int count = dtLeaveService.getTodayLeaveCount();
        return success(count);
    }
}
