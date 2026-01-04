package com.kc.common.utils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 时间工具类
 *
 * @author kc
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils
{
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate()
    {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate()
    {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime()
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow()
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date)
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts)
    {
        try
        {
            return new SimpleDateFormat(format).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str)
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return parseDate(str.toString(), parsePatterns);
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate()
    {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算相差天数
     */
    public static int differentDaysByMillisecond(Date date1, Date date2)
    {
        return Math.abs((int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24)));
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate)
    {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 增加 LocalDateTime ==> Date
     */
    public static Date toDate(LocalDateTime temporalAccessor)
    {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 增加 LocalDate ==> Date
     */
    public static Date toDate(LocalDate temporalAccessor)
    {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * @description:  返回yyyy-MM-dd HH:mm:ss格式的时间类型对象
     * @author: zhaobo.yang
     * @date: 2022/11/26 20:35
     */
    public static Date getYYYYMMDDHHSSMM(){
        String time = DateUtils.getTime();
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now;
        try {
            now = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return now;
    }

    /**
     * @description:  返回yyyy-MM-dd格式的时间类型对象
     * @author: zhaobo.yang
     * @date: 2022/11/26 20:35
     */
    public static Date getYYYYMMDD(){
        String time = DateUtils.getTime();
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
        Date now;
        try {
            now = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return now;
    }

    /**
     * @param timeStamp 打卡时间时间戳
     * @description: 根据时间戳获取统计时间
     * @return:
     * @author: zhaobo.yang
     * @date: 2023/7/7 18:15
     */
    public static String timeConvert(Long timeStamp) {
        //如果打卡时间在今天凌晨 6:00之前，则日期批次号为昨天，如果打卡为今天早上6点之后，则批次号为今天
        Date userCheckTime = new Date(timeStamp);
        String currentDate = DateUtils.parseDateToStr("yyyy-MM-dd", userCheckTime);

        //凌晨六点的时间
        String sixStr = currentDate + " 06:00:00";
        Date sixDate = DateUtils.parseDate(sixStr);
        //凌晨零点的时间
        String zeroStr = currentDate + " 00:00:00";
        Date zeroDate = DateUtils.parseDate(zeroStr);

        int amount = 0;
        if (userCheckTime.compareTo(sixDate) == -1 && userCheckTime.compareTo(zeroDate) != -1) {
            //表明当前打卡时间在凌晨零点和早上六点之间，则日期批次号为昨天
            amount = -1;
        }

        Calendar instance = Calendar.getInstance();
        instance.setTime(userCheckTime);
        instance.add(Calendar.DAY_OF_MONTH, amount);
        Date time = instance.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(time);
    }



    /**
     * @param timeStamp 打卡时间时间戳
     * @description: 根据时间戳获取批次号
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/7 18:15
     */
    public static String getBatchNo(Long timeStamp) {
        //如果打卡时间在今天凌晨 6:00之前，则日期批次号为昨天，如果打卡为今天早上6点之后，则批次号为今天
        Date userCheckTime = new Date(timeStamp);
        String currentDate = DateUtils.parseDateToStr("yyyy-MM-dd", userCheckTime);

        //凌晨六点的时间
        String sixStr = currentDate + " 06:00:00";
        Date sixDate = DateUtils.parseDate(sixStr);
        //凌晨零点的时间
        String zeroStr = currentDate + " 00:00:00";
        Date zeroDate = DateUtils.parseDate(zeroStr);

        int amount = 0;
        if (userCheckTime.compareTo(sixDate) == -1 && userCheckTime.compareTo(zeroDate) != -1) {
            //表明当前打卡时间在凌晨零点和早上六点之间，则日期批次号为昨天
            amount = -1;
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(userCheckTime);
        instance.add(Calendar.DAY_OF_MONTH, amount);
        Date time = instance.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(time);
    }

   public static Date getYesterday(){
       Calendar instance = Calendar.getInstance();
       instance.setTime(new Date());
       instance.add(Calendar.DAY_OF_MONTH, -1);
       instance.set(Calendar.MILLISECOND, 0);
       return instance.getTime();
   }

    public  static String convertFromBeijingTimeToUtc(Date beijingDate) {

        final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

        ZonedDateTime beijingZonedTime = beijingDate.toInstant()
                .atZone(BEIJING_ZONE)
                .toLocalDate().atStartOfDay()
                .atZone(BEIJING_ZONE);

        // 2. 转换为UTC时间并格式化为ISO 8601字符串
        return beijingZonedTime.withZoneSameInstant(ZoneOffset.UTC)
                .format(ISO_FORMATTER);
    }
    public static Date convertFromUtcToBeijingTime(String iso8601Str) {
        Instant utcInstant = Instant.from(ISO_FORMATTER.parse(iso8601Str));
        ZonedDateTime beijingZonedTime = utcInstant.atZone(BEIJING_ZONE);
        LocalDate beijingDate = beijingZonedTime.toLocalDate();
        ZonedDateTime beijingStartOfDay = beijingDate.atStartOfDay(BEIJING_ZONE);
        return Date.from(beijingStartOfDay.toInstant());
    }
}
