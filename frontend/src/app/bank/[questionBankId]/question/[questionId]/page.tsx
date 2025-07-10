"use server";
import { getQuestionBankVoByIdUsingGet } from "@/api/questionBankController";
import { getQuestionVoByIdUsingGet } from "@/api/questionController";
import { Flex, Menu } from "antd";
import Title from "antd/es/typography/Title";
import Sider from "antd/es/layout/Sider";
import { Content } from "antd/es/layout/layout";
import QuestionCard from "@/app/components/QuestionCard";
import Link from "next/link";

export default async function BankQuestionPage({ params }) {
  const { questionBankId, questionId } = params;

  let bank = undefined;
  try {
    const bankRes = await getQuestionBankVoByIdUsingGet({
      id: questionBankId,
      needQueryQuestionList: true,
      pageSize: 200,
    });
    bank = bankRes.data;
  } catch (e) {
    console.error("fail to get the question，" + e.message);
  }
  if (!bank) {
    return <div>fail to get the question bank list</div>;
  }

  let question = undefined;
  try {
    const questionRes = await getQuestionVoByIdUsingGet({
      id: questionId,
    });
    question = questionRes.data;
  } catch (e) {
    console.error("Fail to get the question，" + e.message);
  }
  if (!question) {
    return <div>ail to get the question，please refresh</div>;
  }
  // question menu list

  const questionMenuItemList = (bank.questionPage?.records || []).map(
    (question) => {
      return {
        label: (
          <Link
            href={`/bank/${bank.id}/question/${question.id}`}
            prefetch={false}
          >
            {question.title}
          </Link>
        ),
        key: question.id,
      };
    },
  );

  return (
    <div id="bankQuestionPage">
      <Flex gap={24}>
        <Sider width={240} theme="light" style={{ padding: "24px 0" }}>
          <Title level={4} style={{ padding: "0 20px" }}>
            Questions
          </Title>
          <Menu items={questionMenuItemList} selectedKeys={[questionId]} />
        </Sider>
        <Content>
          <QuestionCard question={question}></QuestionCard>
        </Content>
      </Flex>
    </div>
  );
}
