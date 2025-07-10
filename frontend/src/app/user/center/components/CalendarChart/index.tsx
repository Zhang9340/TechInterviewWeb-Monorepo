/*Search bar */
import { useEffect, useState } from "react";
import dayjs from "dayjs";
import { Props } from "tippy.js";
import ReactECharts from "echarts-for-react";
import { getUserSignInRecordUsingGet } from "@/api/userController";
import { message } from "antd";
export const CalendarChart = (props: Props) => {
  // List of sign-in days ([1, 200] means the 1st and 200th days of the year have sign-in records)
  const [dataList, setDataList] = useState<number[]>([]);
  // 请求后端获取数据
  const fetchDataList = async () => {
    try {
      const res = await getUserSignInRecordUsingGet({
        year,
      });
      // @ts-ignore
      setDataList(res.data || []);
    } catch (e) {
      // @ts-ignore
      message.error("获取刷题签到记录失败，" + e.message);
    }
  };

  useEffect(() => {
    fetchDataList();
  }, []);

  // Calculate the data required for the chart
  const year = new Date().getFullYear();
  const optionsData = dataList.map((dayOfYear, index) => {
    // Calculate the date string
    const dateStr = dayjs(`${year}-01-01`)
      .add(dayOfYear - 1, "day") // Add (dayOfYear - 1) days to January 1st of the current year
      .format("YYYY-MM-DD"); // Format the resulting date as "YYYY-MM-DD"
    return [dateStr, 1]; // Return the formatted date and the value 1
  });

  // Chart configuration
  const options = {
    visualMap: {
      show: false, // Do not display the visual map
      min: 0,
      max: 1,
      inRange: {
        // Color gradient from gray to light green
        color: ["#efefef", "lightgreen"],
      },
    },
    calendar: {
      range: year, // The calendar covers the entire current year
      left: 20, // Left margin
      // Automatically adjust cell width, with a fixed height of 16 pixels
      cellSize: ["auto", 16],
      yearLabel: {
        position: "top", // Place the year label at the top
        formatter: `${year} Year Problem-Solving Record`, // Label text
      },
    },
    series: {
      type: "heatmap", // Use a heatmap to display data
      coordinateSystem: "calendar", // Align the heatmap with the calendar
      data: optionsData, // Data for the heatmap (from the previous mapping)
    },
  };

  return <ReactECharts option={options} />;
};
