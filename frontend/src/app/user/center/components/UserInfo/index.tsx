import React from "react";



import Paragraph from "antd/es/typography/Paragraph";


import "./index.css";





interface Props {


  user: API.LoginUserVO;


}





/**


 * User Profile


 * @param props


 * @constructor


 */


const UserInfo = (props: Props) => {


  const { user } = props;





  return (


    <div className="user-info">


      <div style={{ textAlign: "left" }}>


        {/* phone number */}


        <Paragraph type="secondary">


          Phone Number：{user.phoneNumber || "Blank"}


        </Paragraph>


        {/* Email */}


        <Paragraph type="secondary">Email：{user.email || "Blank"}</Paragraph>





        {/* Grade */}


        <Paragraph type="secondary">Grade：{user.grade || "Blank"}</Paragraph>




        {/* workExperience */}


        <Paragraph type="secondary">


        Work Experience：{user.workExperience ||  "Blank"}


        </Paragraph>





        {/* Expertise */}


        <Paragraph type="secondary">


         Expertise：{user.expertiseDirection || "Blank"}


        </Paragraph>


      </div>


    </div>


  );


};





export default UserInfo;