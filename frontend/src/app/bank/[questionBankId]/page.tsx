"use server";
import Title from "antd/es/typography/Title";
import { getQuestionBankVoByIdUsingGet } from "@/api/questionBankController";
import QuestionList from "@/app/components/QuestionList";
import { Avatar, Button, Card } from "antd";
import Meta from "antd/es/card/Meta";
import Paragraph from "antd/es/typography/Paragraph";
import "./index.css";

/**
 * Question Bank Details Page
 * @constructor
 */
export default async function BankPage({ params }) {
  const { questionBankId } = params;

  let bank = undefined;

  try {
    const bankRes = await getQuestionBankVoByIdUsingGet({
      id: questionBankId,
      needQueryQuestionList: true,
      pageSize: 200,
    });
    bank = bankRes.data;
  } catch (e) {
    console.error("Failed to fetch question bank details, " + e.message);
  }

  if (!bank) {
    return (
      <div>
        Failed to fetch question bank details, please refresh and try again
      </div>
    );
  }
  // get first question id for the start button
  let firstQuestionId;
  if (bank.questionPage?.records && bank.questionPage.records.length > 0) {
    firstQuestionId = bank.questionPage.records[0].id;
  }

  return (
    <div id="bankPage" className="max-width-content">
      <Card>
        <Meta
          avatar={<Avatar src={bank.picture} size={72} />}
          title={
            <Title level={3} style={{ marginBottom: 0 }}>
              {bank.title}
            </Title>
          }
          description={
            <>
              <Paragraph type="secondary">{bank.description}</Paragraph>
              <Button
                type="primary"
                shape="round"
                disabled={!firstQuestionId}
                href={`/bank/${questionBankId}/question/${firstQuestionId}`}
                target="_blank"
              >
                Start
              </Button>
            </>
          }
        ></Meta>
      </Card>
      <div style={{ marginBottom: 16 }} />
      <QuestionList
        questionBankId={questionBankId}
        questionList={bank.questionPage?.records || []}
        cardTitle={`Question List (${bank.questionPage?.total | 0})`}
      />
    </div>
  );
}
