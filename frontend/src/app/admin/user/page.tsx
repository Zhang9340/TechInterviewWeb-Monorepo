"use client";
import CreateModal from "@/app/admin/bank/components/CreateModal";
import UpdateModal from "@/app/admin/bank/components/UpdateModal";
import {
  deleteUserUsingPost,
  listUserByPageUsingPost,
} from "@/api/userController";
import { PlusOutlined } from "@ant-design/icons";
import type { ActionType, ProColumns } from "@ant-design/pro-components";
import { enUSIntl, PageContainer, ProTable } from "@ant-design/pro-components";
import { Button, message, Space, Typography } from "antd";
import React, { useRef, useState } from "react";
import { ValueType } from "@rc-component/mini-decimal";
/**
 * user Management Page
 *
 * @constructor
 */
const UserAdminPage: React.FC = () => {
  // Whether to display the create modal
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  // Whether to display the update modal
  const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  // Data of the currently clicked user
  const [currentRow, setCurrentRow] = useState<API.User>();

  /**
   * Delete user
   *
   * @param row
   */
  const handleDelete = async (row: API.User) => {
    const hide = message.loading("Deleting...");
    if (!row) return true;
    try {
      await deleteUserUsingPost({
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
  const columns: ProColumns<API.User>[] = [
    {
      title: "ID",
      dataIndex: "id",
      valueType: "text",
      hideInForm: true,
    },
    {
      title: "Account",
      dataIndex: "userAccount",
      valueType: "text",
    },
    {
      title: "Username",
      dataIndex: "userName",
      valueType: "text",
    },
    {
      title: "Avatar",
      dataIndex: "userAvatar",
      valueType: "image",
      fieldProps: {
        width: 64,
      },
      hideInSearch: true,
    },
    {
      title: "Profile",
      dataIndex: "userProfile",
      valueType: "textarea",
    },
    {
      title: "Role",
      dataIndex: "userRole",
      valueEnum: {
        user: {
          text: "User",
        },
        admin: {
          text: "Administrator",
        },
      },
    },
    {
      title: "Created At",
      sorter: true,
      dataIndex: "createTime",
      valueType: "dateTime",
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: "Updated At",
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
      <ProTable<API.User>
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

          const { data, code } = await listUserByPageUsingPost({
            ...params,
            sortField,
            sortOrder,
            ...filter,
          } as API.UserQueryRequest);

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
export default UserAdminPage;
