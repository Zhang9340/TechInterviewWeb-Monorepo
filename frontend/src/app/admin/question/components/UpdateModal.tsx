import { updateQuestionUsingPost } from "@/api/questionController";
import { ProColumns, ProTable } from "@ant-design/pro-components";
import { message, Modal } from "antd";
import React from "react";

interface Props {
  oldData?: API.Question;
  visible: boolean;
  columns: ProColumns<API.Question>[];
  onSubmit: (values: API.QuestionAddRequest) => void;
  onCancel: () => void;
}

/**
 * Update a question
 *
 * @param fields
 */
const handleUpdate = async (fields: API.QuestionUpdateRequest) => {
  const hide = message.loading("Updating...");
  try {
    await updateQuestionUsingPost(fields);
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

  let initValues = { ...oldData };
  if (!oldData || oldData.tags) {
    if (oldData) {
      initValues.tags = JSON.parse(oldData.tags as string) || [];
    }
  }

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
          initialValues: initValues,
        }}
        onSubmit={async (values: API.QuestionAddRequest) => {
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
