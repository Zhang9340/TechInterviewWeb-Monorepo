import { addUserUsingPost } from "@/api/userController";
import { ProColumns, ProTable } from "@ant-design/pro-components";
import { message, Modal } from "antd";
import React from "react";
interface Props {
  visible: boolean;
  columns: ProColumns<API.User>[];
  onSubmit: (values: API.UserAddRequest) => void;
  onCancel: () => void;
}

/**
 * Add a new user
 * @param fields
 */
const handleAdd = async (fields: API.UserAddRequest) => {
  const hide = message.loading("Adding...");
  try {
    await addUserUsingPost(fields);
    hide();
    message.success("Created successfully");
    return true;
  } catch (error: any) {
    hide();
    message.error("Creation failed, " + error.message);
    return false;
  }
};

/**
 * Create modal
 * @param props
 * @constructor
 */
const CreateModal: React.FC<Props> = (props) => {
  const { visible, columns, onSubmit, onCancel } = props;

  return (
    <Modal
      destroyOnClose
      title={"Create"}
      open={visible}
      footer={null}
      onCancel={() => {
        onCancel?.();
      }}
    >
      <ProTable
        type="form"
        columns={columns}
        onSubmit={async (values: API.UserAddRequest) => {
          const success = await handleAdd(values);
          if (success) {
            onSubmit?.(values);
          }
        }}
      />
    </Modal>
  );
};
export default CreateModal;
