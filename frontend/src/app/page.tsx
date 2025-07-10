export const dynamic = "force-dynamic";
import Title from "antd/es/typography/Title";
import { Divider, Flex } from "antd";
import "./index.css";
import Link from "next/link";
import { listQuestionBankVoByPageUsingPost } from "@/api/questionBankController";
import { listQuestionVoByPageUsingPost } from "@/api/questionController";
import QuestionBankList from "@/app/components/QuestionBankList";
import QuestionList from "@/app/components/QuestionList";

/**
 * Homepage
 * @constructor
 */
export default async function HomePage() {
  let questionBankList = [];
  let questionList = [];

  try {
    const questionBankRes = await listQuestionBankVoByPageUsingPost({
      pageSize: 12,
      sortField: "createTime",
      sortOrder: "descend",
    });
    questionBankList = questionBankRes.data.records ?? [];
  } catch (e) {
    console.error("Failed to fetch question bank list, " + e.message);
  }

  try {
    const questionListRes = await listQuestionVoByPageUsingPost({
      pageSize: 12,
      sortField: "createTime",
      sortOrder: "descend",
    });
    questionList = questionListRes.data.records ?? [];
  } catch (e) {
    console.error("Failed to fetch question list, " + e.message);
  }
  return (
    <div id="homePage" className="max-width-content">
      <Flex justify="space-between" align="center">
        <Title level={3}>Latest Question Bank</Title>

        <Link href={"/banks"}>Check More </Link>
      </Flex>
      <QuestionBankList questionBankList={questionBankList}></QuestionBankList>
      <div>Question Bank List</div>
      <Divider />
      <Flex justify="space-between" align="center">
        <Title level={3}>Latest Questions</Title>
        <Link href={"/questions"}>Check More </Link>
      </Flex>
      <div>Question List</div>
      <QuestionList questionList={questionList}></QuestionList>
    </div>
  );
}
