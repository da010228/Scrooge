import React, { useState } from "react";
import styles from "./ReportWeek.module.css";
import ReportChart from "./ReportChart"

// 날짜 계산 -> 날짜 이동 탭에 활용 
const getWeekRange = (date) => {
  const startOfWeek = new Date(date);
  const dayOfWeek = date.getDay();
  startOfWeek.setDate(date.getDate() - dayOfWeek + (dayOfWeek === 0? -6 : 1)); // 월요일
  const endOfWeek = new Date(startOfWeek);
  endOfWeek.setDate(startOfWeek.getDate() + 6); // 일요일

  const startDateStr = startOfWeek.toLocaleDateString("ko-KR").slice(2)
  const endDateStr = endOfWeek.toLocaleDateString("ko-KR").slice(2); // MM.dd 형식으로 출력

  return `${startDateStr} ~ ${endDateStr}`;
};

const ReportWeek = () => {
  // 날짜
  const [currentDate, setCurrentDate] = useState(new Date());

  const handleLastsWeek = () => {
    const lastWeek = new Date(currentDate);
    lastWeek.setDate(currentDate.getDate() -7);
    setCurrentDate(lastWeek);
  };
  const handleNextWeek = () => {
    const nextWeek = new Date(currentDate);
    nextWeek.setDate(currentDate.getDate() + 7);
    setCurrentDate(nextWeek);
  };


  return (
    <div className={styles.reportContainer}>
      {/* 소비 날짜 */}
      <div className={styles.weekDays}>
        <button onClick={handleLastsWeek}>
          <img
            src={`${process.env.PUBLIC_URL}/images/left.svg`}
            alt="이전"
          />
        </button>
        <h3>{getWeekRange(currentDate)}</h3>
        <button onClick={handleNextWeek}>
          <img
            src={`${process.env.PUBLIC_URL}/images/right.svg`}
            alt="다음"
          />
        </button>
      </div>

      {/* 소비 차트 */}
      <div className={styles.chart}>
        <ReportChart currentDate={currentDate}/>
      </div>

      <div className={styles.cardContainer}>
        <div>카테고리 카드1</div>
        <div>카테고리 카드2</div>
      </div>
    </div>


  );
};

export default ReportWeek;