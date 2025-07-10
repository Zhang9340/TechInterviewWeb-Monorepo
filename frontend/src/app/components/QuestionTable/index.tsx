"use client";

import { ActionType, ProColumns, ProTable } from "@ant-design/pro-components";
import React, { useRef, useState } from "react";
import Link from "next/link";
import TagList from "@/app/components/TagList";
import {
  listQuestionByPageUsingPost,
  listQuestionVoByPageUsingPost,
  searchQuestionVoByPageUsingPost,
} from "@/api/questionController";
import { TablePaginationConfig } from "antd";

interface Props {
  // Default values (used for displaying server-rendered data)
  defaultQuestionList?: API.QuestionVO[];
  defaultTotal?: number;
  // Default search parameters
  defaultSearchParams?: API.QuestionQueryRequest;
}

/**
 * Question Table Component
 *
 * @constructor
 */
const QuestionTable: React.FC = (props: Props) => {
  const { defaultQuestionList, defaultTotal, defaultSearchParams = {} } = props;
  const actionRef = useRef<ActionType>();
  // List of questions
  const [questionList, setQuestionList] = useState<API.QuestionVO[]>(
    defaultQuestionList || [],
  );
  // Total number of questions
  const [total, setTotal] = useState<number>(defaultTotal || 0);
  // Used to check if this is the first load
  const [init, setInit] = useState<boolean>(true);

  /**
   * Table column configuration
   */
  const columns: ProColumns<API.QuestionVO>[] = [
    {
      title: "Search",
      dataIndex: "searchText",
      valueType: "text",
      hideInTable: true,
    },
    {
      title: "Title",
      dataIndex: "title",
      valueType: "text",
      hideInSearch: true,
      render: (_, record) => {
        return <Link href={`/question/${record.id}`}>{record.title}</Link>;
      },
    },
    {
      title: "Tags",
      dataIndex: "tagList",
      valueType: "select",
      fieldProps: {
        mode: "tags",
      },
      render: (_, record) => {
        return <TagList tagList={record.tagList} />;
      },
    },
  ];

  return (
    <div className="question-table">
      <ProTable<API.QuestionVO>
        actionRef={actionRef}
        size="large"
        search={{
          labelWidth: "auto",
        }}
        form={{
          initialValues: defaultSearchParams,
        }}
        dataSource={questionList}
        pagination={
          {
            pageSize: 12,
            showTotal: (total) => `Total ${total} items`,
            showSizeChanger: false,
            total,
          } as TablePaginationConfig
        }
        request={async (params, sort, filter) => {
          // first request
          if (init) {
            setInit(false);
            // no need for request if have datat
            if (defaultQuestionList && defaultTotal) {
              return;
            }
          }

          const sortField = Object.keys(sort)?.[0] || "createTime";
          const sortOrder = sort?.[sortField] || "descend";

          const { data, code } = await searchQuestionVoByPageUsingPost({
            ...params,
            sortField,
            sortOrder,
            ...filter,
          } as API.QuestionQueryRequest);

          // update data
          const newData = data?.records || [];
          const newTotal = data?.total || 0;
          // updata state
          setQuestionList(newData);
          setTotal(newTotal);

          return {
            success: code === 0,
            data: newData,
            total: newTotal,
          };
        }}
        columns={columns}
      />
    </div>
  );
};
export default QuestionTable;
