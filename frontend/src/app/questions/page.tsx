"use server";
import Title from "antd/es/typography/Title";
import QuestionTable from "@/app/components/QuestionTable";
import {
  listQuestionVoByPageUsingPost,
  searchQuestionVoByPageUsingPost,
} from "@/api/questionController";
import "./index.css";

// @ts-ignore
/**
 * Questions List Page
 * @constructor
 */
export default async function QuestionsPage({ searchParams }) {
  // get url search params
  const { q: searchText } = searchParams;
  let questionList = [];
  let total = 0;

  try {
    const questionRes = await searchQuestionVoByPageUsingPost({
      searchText,
      pageSize: 12,
      sortField: "createTime",
      sortOrder: "desc",
    });
    questionList = questionRes.data.records ?? []; // Assign the fetched records to the question list
    total = questionRes.data.total ?? 0; // Assign the total number of records
  } catch (e) {
    console.error("Failed to fetch the questions list, " + e.message); // Log an error if the API call fails
  }

  return (
    <div id="questionsPage" className="max-width-content">
      <Title level={3}>Questions Repository</Title>
      <QuestionTable
        defaultQuestionList={questionList}
        defaultTotal={total}
        defaultSearchParams={searchText}
      ></QuestionTable>
    </div>
  );
}
