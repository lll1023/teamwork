const URL = "http://localhost:8080/expressions/";
const postMethods = (path, data) => {
  return fetch(URL + path, {
    method: "POST",
    body: data,
    mode: "cors",
  });
};
const $ = (ele) => document.getElementById(ele);

// 生成器部分

let num_obj = $("num");
let max_obj = $("max");
let generate_obj = $("generate");
let down_question_obj = $("down_questions");
let down_answer_obj = $("down_answers");
let down_question_url = null;
let down_answer_url = null;

generate_obj.addEventListener("click", function () {
  if (num_obj.value == null || max_obj.value == null) {
    alert("请输入相关参数");
    return;
  }
  let data = new FormData();
  data.set("num", num_obj.value);
  data.set("maxValue", max_obj.value);
  generate_obj.innerHTML = "生成题目中...";
  postMethods("generateExpressions", data)
    .then((res) => res.json())
    .then((data) => {
      if (data.code == 200) {
        generate_obj.innerHTML = "生成成功！点击下方按钮下载";
        [down_question_url, down_answer_url] = data.result;
      } else {
        generate_obj.innerHTML = "生成题目和答案";
        alert("请求失败，请查看服务器日志或重新启动服务器重试");
      }
    });
});

down_question_obj.addEventListener("click", function () {
  if (down_question_url == null) {
    alert("请先生成题目！");
    return;
  }
  let data = new FormData();
  data.set("fileName", down_question_url);
  postMethods("download", data)
    .then((res) => res.blob())
    .then((data) => {
      let blob = new Blob([data]);
      let filename = "questions" + ".txt";
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(blob);
      link.download = filename;
      link.click();
      window.URL.revokeObjectURL(link.href)
    });
});

down_answer_obj.addEventListener("click", function () {
  if (down_answer_url == null) {
    alert("请先生成题目！");
    return;
  }
  let data = new FormData();
  data.set("fileName", down_answer_url);
  postMethods("download", data)
    .then((res) => res.blob())
    .then((data) => {
      let blob = new Blob([data]);
      let filename = "answers.txt";
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(blob);
      link.download = filename;
      link.click();
      window.URL.revokeObjectURL(link.href)
    });
});

// 校验器部分
let up_questions_btn = $("up_questions");
let up_answers_btn = $("up_answers");
let check_btn = $("check");

let up_questions_file = null;
let up_answers_file = null;

up_questions_btn.addEventListener("click", function () {
  let input = document.createElement("input");
  input.type = "file";
  input.accept = ".txt";
  input.addEventListener("change", function (e) {
    up_questions_file = input.files[0];
    up_questions_btn.innerHTML = "已选择:" + up_questions_file.name;
  });
  input.click();
});

up_answers_btn.addEventListener("click", function () {
  let input = document.createElement("input");
  input.type = "file";
  input.accept = ".txt";
  input.addEventListener("change", function (e) {
    up_answers_file = input.files[0];
    up_answers_btn.innerHTML = "已选择:" + up_answers_file.name;
  });
  input.click();
});


check_btn.addEventListener('click',function() {
    if(up_questions_file == null || up_answers_file == null) {
        alert("请上传文件！");
        return;
    }
    check_btn.innerHTML = "检验中..."
    let data = new FormData();
    console.log(up_questions_file,up_answers_file)
    data.append("files",up_questions_file,'questions.txt');
    data.append("files",up_answers_file,'answers.txt');
    postMethods('check',data).then(res => {
        if(res.status != 200) {
            check_btn.innerHTML = "自动校验"
            alert('请上传正确的文件');
            return;
        }
        return res.blob();

    }).then(data => {
        if(data == null) {
            return;
        }
        let blob = new Blob([data]);
        let filename = "result.txt";
        let link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = filename;
        link.click();
        window.URL.revokeObjectURL(link.href)
        check_btn.innerHTML = "校验成功"
    })
})
