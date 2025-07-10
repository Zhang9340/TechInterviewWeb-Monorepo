"use client";
import { Card, List, Tag } from "antd";
import "./index.css";
import Link from "next/link";
import React from "react";

interface Props {
  questionList: API.QuestionVO[];
  questionBankId?: number;
  cardTitle?: React.ReactNode;
}

/**
 * Question List Component
 * @param props
 * @constructor
 */
const QuestionList = (props: Props) => {
  const { questionList = [], cardTitle, questionBankId } = props;

  const tagList = (tags: string[] = []) => {
    return tags.map((tag) => {
      return <Tag key={tag}>{tag}</Tag>;
    });
  };

  return (
    <Card className="question-list" title={cardTitle}>
      <List
        dataSource={questionList}
        renderItem={(item: API.QuestionVO) => (
          <List.Item extra={tagList(item.tagList)}>
            <List.Item.Meta
              title={
                <Link
                  href={
                    questionBankId
                      ? `/bank/${questionBankId}/question/${item.id}`
                      : `/question/${item.id}`
                  }
                >
                  {item.title}
                </Link>
              }
            />
          </List.Item>
        )}
      />
    </Card>
  );
};

export default QuestionList;
