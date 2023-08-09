import { useEffect, useState } from "react";
import { useSelector } from "react-redux";

import ProgressBar from "./ProgressBar";
import styles from "./Main.module.css";
import CharacterCard from "../../components/UI/CharacterCard";
import Card from "../../components/UI/Card";
import BackGround from "../../components/BackGround";
import PaymentHistory from "../../pages/Main/PaymentHistory";

const Main = (props) => {
  const globalToken = useSelector((state) => state.globalToken);

  const [data, setData] = useState([]);
  const [total, setTotal] = useState();
  const [date, setDate] = useState([]);

  const [settlement, setSettlement] = useState(false);

  const [weeklyGoal, setWeeklyGoal] = useState();
  const [weeklyConsum, setWeeklyConsum] = useState();

  useEffect(() => {
    getCurrentDate();
    console.log(globalToken);
    const postData = {
      method: "GET",
      headers: {
        Authorization: globalToken,
      },
    };
    fetch("https://day6scrooge.duckdns.org/api/member/info", postData)
      .then((resp) => resp.json())
      .then((data) => {
        setData(data);
        setWeeklyGoal(data.weeklyGoal);
        setWeeklyConsum(data.weeklyConsum);
      })
      .catch((error) => console.log(error));
  }, []);

  const getCurrentDate = () => {
    const today = new Date();
    const month = today.getMonth() + 1;
    const day = today.getDate();
    setDate([month, day]);
  };

  //주간 목표 설정
  const setGoal = (goal) => {
    const obj = { weeklyGoal: goal };
    const postData = {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: globalToken,
      },
      body: JSON.stringify(obj),
    };
    fetch(`https://day6scrooge.duckdns.org/api/member/weekly-goal`, postData)
      .then((res) => res.json())
      .then((data) => {
        setWeeklyGoal(data.weeklyGoal);
      })
      .then(console.log);
  };

  const getTotal = () => {
    const postData = {
      method: "GET",
      headers: {
        Authorization: globalToken,
      },
    };
    fetch(
      "https://day6scrooge.duckdns.org/api/payment-history/today-total",
      postData
    )
      .then((resp) => resp.json())
      .then((data) => {
        setTotal(data);
      })
      .catch((error) => console.log(error));
  };

  const [isConsum, setIsConsum] = useState(false);

  const consumTrueHandler = () => {
    setIsConsum(true);
  };

  const consumFalseHandler = () => {
    setIsConsum(false);
  };

  const settlementTrueHandler = () => {
    setSettlement(true);
  };

  const settlementFalseHandler = () => {
    setSettlement(false);
  };

  return (
    <BackGround>
      {!isConsum && data && data.levelId && (
        <div>
          <div className={styles.empty} />
          <CharacterCard>
            <div>
              <div className={styles.infoheader}>
                <img
                  className={styles.badge}
                  src={`${process.env.PUBLIC_URL}/images/sample-badge.svg`}
                  alt="뱃지"
                />
                <span>
                  <p>Lv. {data.levelId}</p>
                  <p>{data.nickname}</p>
                </span>
              </div>
              <div className={styles.border} />

              <span className={styles.charactercoin}>
                <img
                  className={styles.character}
                  src={
                    data.mainAvatar
                      ? `${process.env.PUBLIC_URL}/Character/${data.mainAvatar}.png`
                      : `${process.env.PUBLIC_URL}/Character/1.png`
                  }
                  alt="캐릭터"
                />

                <div className={styles.coin} onClick={consumTrueHandler}>
                  <img
                    src={`${process.env.PUBLIC_URL}/images/coin.png`}
                    alt="코인"
                  />
                  <div className={styles.payBtn}>정산하기</div>
                </div>
              </span>
              <div className={styles.statemessage}>
                <div>
                  상태메세지 <br />
                  에엥
                  <br />
                  괜찮나?
                </div>
                <img
                  className={styles.editBtn}
                  src={`${process.env.PUBLIC_URL}/images/write.svg`}
                  alt="코인"
                />
              </div>
            </div>
          </CharacterCard>
          <div className={styles.rings}>
            <img
              className={styles.ring}
              src={`${process.env.PUBLIC_URL}/images/main-ring.png`}
              alt="고리"
            />
            <img
              className={styles.ring}
              src={`${process.env.PUBLIC_URL}/images/main-ring.png`}
              alt="고리"
            />
            <img
              className={styles.ring}
              src={`${process.env.PUBLIC_URL}/images/main-ring.png`}
              alt="고리"
            />
          </div>
          <Card height={220}>
            <div className={styles.todayCard}>
              <div className={styles.title}>
                {date[0]}월 {date[1]}일, 오늘의 소비💸
              </div>
              <div className={styles.amount}>
                {settlement ? `${total}원` : "정산이 필요해요!"}
              </div>
            </div>
            <ProgressBar
              goal={weeklyGoal}
              consum={weeklyConsum}
              setGoal={setGoal}
            ></ProgressBar>
          </Card>
        </div>
      )}
      {isConsum && (
        <div>
          <PaymentHistory
            total={total}
            getTotal={getTotal}
            consumFalseHandler={consumFalseHandler}
            settlement={settlement}
            settlementTrueHandler={settlementTrueHandler}
            settlementFalseHandler={settlementFalseHandler}
          />
        </div>
      )}
    </BackGround>
  );
};

export default Main;
