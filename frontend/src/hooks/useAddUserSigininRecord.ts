import { useEffect, useState } from "react";
import { message } from "antd";
import { addUserSignInUsingPost } from "@/api/userController";

/**
 * 添加用户签到记录钩子
 */
const useAddUserSignInRecord = () => {
  const [loading, setLoading] = useState(false);

  // 请求后端执行签到
  const doFetch = async () => {
    setLoading(true);
    try {
      await addUserSignInUsingPost();
    } catch (e) {
      // @ts-ignore
      message.error("checkin failure，" + e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    doFetch();
  }, []);

  return { loading };
};

export default useAddUserSignInRecord;
