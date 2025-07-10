import React from "react";
import "./index.css";
/*
 *Global footer  */

export default function GlobalFooter() {
  const curYear: number = new Date().getFullYear();
  return (
    <div className="global-footer">
      <div>© {curYear} Tech Interview Preparation </div>
      <div>by Zhiyuan(Charlie) Zhang</div>
    </div>
  );
}
