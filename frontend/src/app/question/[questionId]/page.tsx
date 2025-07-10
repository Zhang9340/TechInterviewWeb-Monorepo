"use server";
import { getQuestionVoByIdUsingGet } from "@/api/questionController";
import QuestionCard from "@/app/components/QuestionCard";

export default async function QuestionPage({ params }) {
  const { questionId } = params;

  let question = undefined;
  try {
    const questionRes = await getQuestionVoByIdUsingGet({
      id: questionId,
    });
    question = questionRes.data;
  } catch (e) {
    console.error("Fail to get the question，" + e.message);
  }
  if (!question) {
    return <div>ail to get the question，please refresh</div>;
  }

  return (
    <div id="questionPage">
      <QuestionCard question={question}></QuestionCard>
    </div>
  );
}
