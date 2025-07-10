"use client";
import Meta from "antd/es/card/Meta";
import Paragraph from "antd/es/typography/Paragraph";
import Title from "antd/es/typography/Title";
import { useState } from "react";
import "./index.css";
import { CalendarChart } from "@/app/user/center/components/CalendarChart";
import UserInfo from "@/app/user/center/components/UserInfo";
import {Avatar, Card, Col, Row, Segmented, Tag} from "antd";
import {useSelector} from "react-redux";
import {RootState} from "@/stores";

import UserInfoEditForm from "@/app/user/center/components/UserInfoEditForm";


import {USER_ROLE_ENUM, USER_ROLE_TEXT_MAP} from "@/constant/user";


import dayjs from "dayjs";
/**
 * User Center Page Component
 * @constructor
 */
export default function UserCenterPage() {
  // Get the logged-in user's information from the Redux store
  const loginUser = useSelector((state: RootState) => state.loginUser);

  // Create a new variable for reuse
  const user = loginUser;

  // State to control the active tab in the menu bar
  const [activeTabKey, setActiveTabKey] = useState<string>("Info");
  const [currentEditState, setCurrentEditState] = useState<string>("Check Info");
  return (

    <div id="userCenterPage" className="max-width-content">

      <Row gutter={[16, 16]}>

        <Col xs={24} md={6}>

          <Card style={{ textAlign: "center" }}>

            <Avatar src={user.userAvatar} size={72} />

            <div style={{ marginBottom: 16 }} />

            <Card.Meta

              title={

                <Title level={4} style={{ marginBottom: 0 }}>

                  {user.userName}

                </Title>

              }

              description={

                <Paragraph type="secondary">{user.userProfile}</Paragraph>

              }

            />


            <Tag


              color={user.userRole === USER_ROLE_ENUM.ADMIN ? "gold" : "grey"}


            >


              {USER_ROLE_TEXT_MAP[user.userRole]}


            </Tag>


            <Paragraph type="secondary" style={{ marginTop: 8 }}>


              Join Date：{dayjs(user.createTime).format("YYYY-MM-DD")}


            </Paragraph>


            <Paragraph type="secondary" style={{ marginTop: 8 }} copyable={{


              text: user.id


            }}>


              My id：{user.id}


            </Paragraph>

          </Card>

        </Col>

        <Col xs={24} md={18}>

          <Card

            tabList={[


              {


                key: "info",


                label: "My Info",


              },

              {

                key: "record",

                label: "Reocrd",

              },

              {

                key: "others",

                label: "others",

              },

            ]}

            activeTabKey={activeTabKey}

            onTabChange={(key: string) => {

              setActiveTabKey(key);

            }}

          >


            {activeTabKey === "info" && (


              <>


                <Segmented<string>


                  options={["Info", "Edit"]}


                  value={currentEditState}


                  onChange={setCurrentEditState}


                />


                {currentEditState === "Info" && <UserInfo user={user} />}


                {currentEditState === "Edit" && (


                  <UserInfoEditForm user={user} />


                )}


              </>


            )}

            {activeTabKey === "record" && (

              <>

                <CalendarChart />

              </>

            )}

            {activeTabKey === "others" && <>bbb</>}

          </Card>

        </Col>

      </Row>

    </div>

  );
}
