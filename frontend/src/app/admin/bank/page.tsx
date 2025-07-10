"use client";
import CreateModal from "@/app/admin/bank/components/CreateModal";
import UpdateModal from "@/app/admin/bank/components/UpdateModal";
import {
  deleteQuestionBankUsingPost,
  listQuestionBankByPageUsingPost,
} from "@/api/questionBankController";
import { PlusOutlined } from "@ant-design/icons";
import type { ActionType, ProColumns } from "@ant-design/pro-components";
import { enUSIntl, PageContainer, ProTable } from "@ant-design/pro-components";
import { Button, message, Space, Typography } from "antd";
import React, { useRef, useState } from "react";
import { ValueType } from "@rc-component/mini-decimal";
/**
 * questionBank Management Page
 *
 * @constructor
 */
const QuestionBankAdminPage: React.FC = () => {
  // Whether to display the create modal
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  // Whether to display the update modal
  const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  // Data of the currently clicked questionBank
  const [currentRow, setCurrentRow] = useState<API.QuestionBank>();

  /**
   * Delete questionBank
   *
   * @param row
   */
  const handleDelete = async (row: API.QuestionBank) => {
    const hide = message.loading("Deleting...");
    if (!row) return true;
    try {
      await deleteQuestionBankUsingPost({
        id: row.id as any,
      });
      hide();
      message.success("Deleted successfully");
      actionRef?.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error("Delete failed, " + error.message);
      return false;
    }
  };

  /**
   * Table column configuration
   */
  const columns: ProColumns<API.QuestionBank>[] = [
    {
      title: "ID",
      dataIndex: "id",
      valueType: "text",
      hideInForm: true,
    },
    {
      title: "Title",
      dataIndex: "title",
      valueType: "text",
    },
    {
      title: "Description",
      dataIndex: "description",
      valueType: "text",
    },
    {
      title: "Image",
      dataIndex: "picture",
      valueType: "image",
      fieldProps: {
        width: 64,
      },
      hideInSearch: true,
    },
    {
      title: "Creation Time",
      sorter: true,
      dataIndex: "createTime",
      valueType: "dateTime",
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: "Edit Time",
      sorter: true,
      dataIndex: "editTime",
      valueType: "dateTime",
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: "Update Time",
      sorter: true,
      dataIndex: "updateTime",
      valueType: "dateTime",
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: "Actions",
      dataIndex: "option",
      valueType: "option",
      render: (_, record) => (
        <Space size="middle">
          <Typography.Link
            onClick={() => {
              setCurrentRow(record);
              setUpdateModalVisible(true);
            }}
          >
            Edit
          </Typography.Link>
          <Typography.Link type="danger" onClick={() => handleDelete(record)}>
            Delete
          </Typography.Link>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer>
      <ProTable<API.QuestionBank>
        headerTitle={"Search Table"}
        actionRef={actionRef}
        rowKey="key"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => {
              setCreateModalVisible(true);
            }}
          >
            <PlusOutlined /> Create
          </Button>,
        ]}
        request={async (params, sort, filter) => {
          const sortField = Object.keys(sort)?.[0];
          const sortOrder = sort?.[sortField] ?? undefined;

          const { data, code } = await listQuestionBankByPageUsingPost({
            ...params,
            sortField,
            sortOrder,
            ...filter,
          } as API.QuestionBankQueryRequest);

          return {
            success: code === 0,
            data: data?.records || [],
            total: Number(data?.total) || 0,
          };
        }}
        columns={columns}
      />
      <CreateModal
        visible={createModalVisible}
        columns={columns}
        onSubmit={() => {
          setCreateModalVisible(false);
          actionRef.current?.reload();
        }}
        onCancel={() => {
          setCreateModalVisible(false);
        }}
      />
      <UpdateModal
        visible={updateModalVisible}
        columns={columns}
        oldData={currentRow}
        onSubmit={() => {
          setUpdateModalVisible(false);
          setCurrentRow(undefined);
          actionRef.current?.reload();
        }}
        onCancel={() => {
          setUpdateModalVisible(false);
        }}
      />
    </PageContainer>
  );
};
export default QuestionBankAdminPage;
