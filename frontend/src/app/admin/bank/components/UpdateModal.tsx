import { updateQuestionBankUsingPost } from "@/api/questionBankController";
import { ProColumns, ProTable } from "@ant-design/pro-components";
import { message, Modal } from "antd";
import React from "react";

interface Props {
  oldData?: API.QuestionBank;
  visible: boolean;
  columns: ProColumns<API.QuestionBank>[];
  onSubmit: (values: API.QuestionBankAddRequest) => void;
  onCancel: () => void;
}

/**
 * Update a questionBank
 *
 * @param fields
 */
const handleUpdate = async (fields: API.QuestionBankUpdateRequest) => {
  const hide = message.loading("Updating...");
  try {
    await updateQuestionBankUsingPost(fields);
    hide();
    message.success("Updated successfully");
    return true;
  } catch (error: any) {
    hide();
    message.error("Update failed, " + error.message);
    return false;
  }
};

/**
 * Update modal
 * @param props
 * @constructor
 */
const UpdateModal: React.FC<Props> = (props) => {
  const { oldData, visible, columns, onSubmit, onCancel } = props;

  if (!oldData) {
    return <></>;
  }

  return (
    <Modal
      destroyOnClose
      title={"Update"}
      open={visible}
      footer={null}
      onCancel={() => {
        onCancel?.();
      }}
    >
      <ProTable
        type="form"
        columns={columns}
        form={{
          initialValues: oldData,
        }}
        onSubmit={async (values: API.QuestionBankAddRequest) => {
          const success = await handleUpdate({
            ...values,
            id: oldData.id as any,
          });
          if (success) {
            onSubmit?.(values);
          }
        }}
      />
    </Modal>
  );
};

export default UpdateModal;
