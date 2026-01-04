package com.kc.common.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kc.common.annotation.Excel;
import com.kc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 节假日对象 dt_holiday
 *
 * @author kc
 * @date 2024-04-30
 */
public class DtHoliday extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 节日名称，工作日时显示“无”，周末时显示“周末”，节日时显示节日名称 */
    @Excel(name = "节日名称，工作日时显示“无”，周末时显示“周末”，节日时显示节日名称")
    private String holiday;

    /** 星期几的数字 */
    @Excel(name = "星期几的数字")
    private String weekDay;

    /** 星期几的中文 */
    @Excel(name = "星期几的中文")
    private String cn;

    /** 节日备注 */
    @Excel(name = "节日备注")
    @JsonProperty(value = "holiday_remark")
    private String holidayRemark;

    /** 星期几的英文名 */
    @Excel(name = "星期几的英文名")
    private String en;

    /** 当前日期yyyyMMdd */
    @Excel(name = "当前日期yyyyMMdd")
    @JsonProperty(value = "day")
    private String curDay;

    /** 1为工作日，2为周末，3为节假日 */
    @Excel(name = "1为工作日，2为周末，3为节假日")
    @JsonProperty(value = "type")
    private String dayType;

    /** 节日或周末结束时间，如果是工作日，此字段为空串 */
    @Excel(name = "节日或周末结束时间，如果是工作日，此字段为空串")
    @JsonProperty(value = "end")
    private String endDay;

    /** 节日或周末开始时间，如果是工作日，此字段为空串 */
    @Excel(name = "节日或周末开始时间，如果是工作日，此字段为空串")
    @JsonProperty(value = "begin")
    private String beginDay;

    /** 删除标识 */
    private String delFlag;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setHoliday(String holiday)
    {
        this.holiday = holiday;
    }

    public String getHoliday()
    {
        return holiday;
    }
    public void setWeekDay(String weekDay)
    {
        this.weekDay = weekDay;
    }

    public String getWeekDay()
    {
        return weekDay;
    }
    public void setCn(String cn)
    {
        this.cn = cn;
    }

    public String getCn()
    {
        return cn;
    }
    public void setHolidayRemark(String holidayRemark)
    {
        this.holidayRemark = holidayRemark;
    }

    public String getHolidayRemark()
    {
        return holidayRemark;
    }
    public void setEn(String en)
    {
        this.en = en;
    }

    public String getEn()
    {
        return en;
    }
    public void setCurDay(String curDay)
    {
        this.curDay = curDay;
    }

    public String getCurDay()
    {
        return curDay;
    }
    public void setDayType(String dayType)
    {
        this.dayType = dayType;
    }

    public String getDayType()
    {
        return dayType;
    }
    public void setEndDay(String endDay)
    {
        this.endDay = endDay;
    }

    public String getEndDay()
    {
        return endDay;
    }
    public void setBeginDay(String beginDay)
    {
        this.beginDay = beginDay;
    }

    public String getBeginDay()
    {
        return beginDay;
    }
    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("holiday", getHoliday())
                .append("weekDay", getWeekDay())
                .append("cn", getCn())
                .append("holidayRemark", getHolidayRemark())
                .append("en", getEn())
                .append("curDay", getCurDay())
                .append("dayType", getDayType())
                .append("endDay", getEndDay())
                .append("beginDay", getBeginDay())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("delFlag", getDelFlag())
                .toString();
    }
}
