"use client";
import CreateModal from "@/app/admin/question/components/CreateModal";
import UpdateModal from "@/app/admin/question/components/UpdateModal";
import {
  deleteQuestionUsingPost,
  listQuestionByPageUsingPost,
  listQuestionVoByPageUsingPost,
} from "@/api/questionController";
import { PlusOutlined } from "@ant-design/icons";
import type { ActionType, ProColumns } from "@ant-design/pro-components";
import { enUSIntl, PageContainer, ProTable } from "@ant-design/pro-components";
import { Button, message, Popconfirm, Space, Table, Typography } from "antd";
import React, { useRef, useState } from "react";
import TagList from "@/app/components/TagList";
import MdEditor from "@/app/components/MdEditor";
import UpdateBankModal from "@/app/admin/question/components/UpdateBankModal";
import BatchAddQuestionsToBankModal from "@/app/admin/question/components/BatchAddQuestionsToBankModal";
import { batchDeleteQuestionsUsingPost } from "@/api/questionBankQuestionController";

function BatchRemoveQuestionsFromBankModal(props: {
  visible: boolean;
  questionIdList: number[];
  onSubmit: () => void;
  onCancel: () => void;
}) {
  return null;
}

/**
 * question Management Page
 *
 * @constructor
 */
const QuestionAdminPage: React.FC = () => {
  // Whether to display the create modal
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  // Whether to display the update modal
  const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  // Data of the currently clicked question
  const [currentRow, setCurrentRow] = useState<API.Question>();
  // If you don't want to display the update, you can't see it
  const [updateBankModalVisible, setUpdateBankModalVisible] =
    useState<boolean>(false);

  // Whether to display the modal for batch adding questions to the question bank
  const [
    batchAddQuestionsToBankModalVisible,
    setBatchAddQuestionsToBankModalVisible,
  ] = useState<boolean>(false);

  // Whether to display the modal for batch removing questions from the question bank
  const [
    batchRemoveQuestionsFromBankModalVisible,
    setBatchRemoveQuestionsFromBankModalVisible,
  ] = useState<boolean>(false);

  // List of currently selected question IDs
  const [selectedQuestionIdList, setSelectedQuestionIdList] = useState<
    number[]
  >([]);

  /**
   * Delete question
   *
   * @param row
   */
  const handleDelete = async (row: API.Question) => {
    const hide = message.loading("Deleting...");
    if (!row) return true;
    try {
      await deleteQuestionUsingPost({
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
   * Batch Delete
   * @param questionIdList
   */
  const handleBatchDelete = async (questionIdList: number[]) => {
    const hide = message.loading("Processing...");
    try {
      await batchDeleteQuestionsUsingPost({
        questionIdList,
      });
      hide();
      message.success("Operation successful");
      actionRef?.current?.reload();
    } catch (error: any) {
      hide();
      message.error("Operation failed: " + error.message);
    }
  };

  /**
   * Table column configuration
   */
  const columns: ProColumns<API.Question>[] = [
    {
      title: "ID", // Column title
      dataIndex: "id", // Maps to the `id` field in the data
      valueType: "text", // Data type is text
      hideInForm: true, // Hide this column in the form
    },
    {
      title: "Title", // Column title
      dataIndex: "title", // Maps to the `title` field in the data
      valueType: "text", // Data type is text
    },
    {
      title: "Question bank",
      dataIndex: "questionBankId",
      hideInTable: true,
      hideInForm: true,
    },

    {
      title: "Content", // Column title
      dataIndex: "content", // Maps to the `content` field in the data
      valueType: "text", // Data type is text
      hideInSearch: true, // Hide this column in the search bar
      width: 240, // Column width
      renderFormItem: (
        _,
        { type, defaultRender, formItemProps, fieldProps, ...rest },
        form,
      ) => {
        return <MdEditor {...fieldProps} />;
      },
    },
    {
      title: "Answer", // Column title
      dataIndex: "answer", // Maps to the `answer` field in the data
      valueType: "text", // Data type is text
      hideInSearch: true, // Hide this column in the search bar
      width: 640, // Column width
      renderFormItem: (
        _,
        { type, defaultRender, formItemProps, fieldProps, ...rest },
        form,
      ) => {
        return <MdEditor {...fieldProps} />;
      },
    },
    {
      title: "Tags", // Column title
      dataIndex: "tags", // Maps to the `tags` field in the data
      valueType: "select", // Data type is a selection field
      fieldProps: {
        mode: "tags", // Enables tag mode for multi-selection
      },
      render: (_, record) => {
        const tagList = JSON.parse(record.tags || "[]");
        return <TagList tagList={tagList} />;
      },
    },
    {
      title: "Created By", // Column title
      dataIndex: "userId", // Maps to the `userId` field in the data
      valueType: "text", // Data type is text
      hideInForm: true, // Hide this column in the form
    },
    {
      title: "Creation Time", // Column title
      sorter: true, // Enable sorting for this column
      dataIndex: "createTime", // Maps to the `createTime` field in the data
      valueType: "dateTime", // Data type is date and time
      hideInSearch: true, // Hide this column in the search bar
      hideInForm: true, // Hide this column in the form
    },
    {
      title: "Edit Time", // Column title
      sorter: true, // Enable sorting for this column
      dataIndex: "editTime", // Maps to the `editTime` field in the data
      valueType: "dateTime", // Data type is date and time
      hideInSearch: true, // Hide this column in the search bar
      hideInForm: true, // Hide this column in the form
    },
    {
      title: "Update Time", // Column title
      sorter: true, // Enable sorting for this column
      dataIndex: "updateTime", // Maps to the `updateTime` field in the data
      valueType: "dateTime", // Data type is date and time
      hideInSearch: true, // Hide this column in the search bar
      hideInForm: true, // Hide this column in the form
    },
    {
      title: "Actions", // Column title
      dataIndex: "option", // Not directly linked to any data field
      valueType: "option", // Indicates operation buttons or links
      render: (_, record) => (
        <Space size="middle">
          <Typography.Link
            onClick={() => {
              setCurrentRow(record);
              setUpdateBankModalVisible(true);
            }}
          >
            edit bank
          </Typography.Link>

          <Typography.Link
            onClick={() => {
              setCurrentRow(record); // Set the selected record for updates
              setUpdateModalVisible(true); // Show the update modal
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
      <ProTable<API.Question>
        headerTitle={"Search Table"}
        actionRef={actionRef}
        rowKey="key"
        search={{
          labelWidth: 120,
        }}
        rowKey="id"
        rowSelection={{
          // Custom selection options reference: https://ant.design/components/table-cn/#components-table-demo-row-selection-custom
          // Comment out this line to not show the dropdown options by default
          selections: [Table.SELECTION_ALL, Table.SELECTION_INVERT],
        }}
        tableAlertRender={({
          selectedRowKeys,
          selectedRows,
          onCleanSelected,
        }) => {
          return (
            <Space size={24}>
              <span>
                Selected {selectedRowKeys.length} items
                <a style={{ marginInlineStart: 8 }} onClick={onCleanSelected}>
                  Deselect
                </a>
              </span>
            </Space>
          );
        }}
        tableAlertOptionRender={({
          selectedRowKeys,
          selectedRows,
          onCleanSelected,
        }) => {
          return (
            <Space size={16}>
              <Button
                onClick={() => {
                  // Open popup
                  setSelectedQuestionIdList(selectedRowKeys as number[]);
                  setBatchAddQuestionsToBankModalVisible(true);
                }}
              >
                Batch Add Questions to the Question Bank
              </Button>
              <Button
                onClick={() => {
                  // Open popup
                  setSelectedQuestionIdList(selectedRowKeys as number[]);
                  setBatchRemoveQuestionsFromBankModalVisible(true);
                }}
              >
                Batch Remove Questions from the Question Bank
              </Button>
              <Popconfirm
                title="Confirm Delete"
                description="Are you sure you want to delete these questions?"
                onConfirm={() => {
                  // Batch delete questions
                  handleBatchDelete(selectedRowKeys as number[]);
                }}
                okText="Yes"
                cancelText="No"
              >
                <Button danger>Batch Delete Questions</Button>
              </Popconfirm>
            </Space>
          );
        }}
        toolBarRender={() => [
          <Button
            type="primary"
            ghost
            key="primary"
            href="/admin/question/ai"
            target="_blank"
          >
            <PlusOutlined /> AI Generate Questions
          </Button>,
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

          const { data, code } = await listQuestionVoByPageUsingPost({
            ...params,
            sortField,
            sortOrder,
            ...filter,
          } as API.QuestionQueryRequest);
          console.log(data);
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
      <UpdateBankModal
        visible={updateBankModalVisible}
        questionId={currentRow?.id}
        onCancel={() => {
          setCurrentRow(undefined);
          setUpdateBankModalVisible(false);
        }}
      />

      <BatchAddQuestionsToBankModal
        visible={batchAddQuestionsToBankModalVisible}
        questionIdList={selectedQuestionIdList}
        onSubmit={() => {
          setBatchAddQuestionsToBankModalVisible(false);
        }}
        onCancel={() => {
          setBatchAddQuestionsToBankModalVisible(false);
        }}
      />
      <BatchRemoveQuestionsFromBankModal
        visible={batchRemoveQuestionsFromBankModalVisible}
        questionIdList={selectedQuestionIdList}
        onSubmit={() => {
          setBatchRemoveQuestionsFromBankModalVisible(false);
        }}
        onCancel={() => {
          setBatchRemoveQuestionsFromBankModalVisible(false);
        }}
      />
    </PageContainer>
  );
};
export default QuestionAdminPage;
