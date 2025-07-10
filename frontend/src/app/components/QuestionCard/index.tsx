"use client";
import { Card } from "antd";
import Title from "antd/es/typography/Title";
import MdViewer from "@/app/components/MdViewer";
import "./index.css";
import TagList from "@/app/components/TagList";
import useAddUserSignInRecord from "@/hooks/useAddUserSigininRecord";

interface Props {
  question: API.QuestionVO;
}

/**
 * Question Card Component
 * @param props
 * @constructor
 */
const QuestionCard = (props: Props) => {
  const { question } = props;
  useAddUserSignInRecord();
  return (
    <div className="question-card">
      <Card bordered={false}>
        <Title level={1} style={{ fontSize: 24 }}>
          {question.title}
        </Title>
        <TagList tagList={question.tagList} />
        <div style={{ marginBottom: 16 }} />
        <MdViewer value={question.content} />
      </Card>
      <div style={{ marginBottom: 16 }} />
      <Card title="Recommended Answer" bordered={false}>
        <MdViewer value={question.answer} />
      </Card>
    </div>
  );
};

export default QuestionCard;
