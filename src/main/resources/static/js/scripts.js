document.getElementById("btnSubmit").onclick = function() {
};

$(document).on("click", "#btnSubmit", function(){
    let summonerNm = $("#summonerNm").val();
    location.href="./summonerInfo/"+summonerNm;
    //location.href="./lol/findUserInfo/"+summonerNm;
});
