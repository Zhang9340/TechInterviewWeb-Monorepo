import { Tag } from "antd";
import "./index.css";

interface Props {
  tagList?: string[]; // Optional list of tags
}

/**
 * Tag List Component
 * @param props
 * @constructor
 */
const TagList = (props: Props) => {
  const { tagList = [] } = props; // Destructure with a default value for the tag list

  return (
    <div className="tag-list">
      {tagList.map((tag) => {
        return <Tag key={tag}>{tag}</Tag>; // Render each tag
      })}
    </div>
  );
};

export default TagList;
