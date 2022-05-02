alert("확인2");

document.getElementById("btnSubmit").onclick = function() {
    alert("클릭함");
};

$(document).on("click", "#btnSubmit", function(){
    alert("btnSubmit 버튼 클릭 이벤트2");
});
