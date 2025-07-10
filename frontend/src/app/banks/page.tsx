// "use server";
// @ts-ignore
export const dynamic = "force-dynamic";
import Title from "antd/es/typography/Title";
import { Divider, Flex } from "antd";
import "./index.css";
import { listQuestionBankVoByPageUsingPost } from "@/api/questionBankController";
import QuestionBankList from "@/app/components/QuestionBankList";

/**
 * Homepage
 * @constructor
 */
export default async function BanksPage() {
  let questionBankList = [];
  const pageSize = 200;
  try {
    const questionBankRes = await listQuestionBankVoByPageUsingPost({
      pageSize: pageSize,
      sortField: "createTime",
      sortOrder: "descend",
    });
    questionBankList = questionBankRes.data.records ?? [];
  } catch (e) {
    console.error("Failed to fetch question bank list, " + e.message);
  }

  return (
    <div id="BanksPage" className="max-width-content">
      <Flex justify="space-between" align="center">
        <Title level={3}>All Question Bank</Title>
      </Flex>
      <QuestionBankList questionBankList={questionBankList}></QuestionBankList>
    </div>
  );
}
