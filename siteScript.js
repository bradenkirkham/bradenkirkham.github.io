function showText(divElement, arrowElement){
    console.log("made it")
    var displayVal = divElement.style.display;

    if(displayVal == 'none'){
        divElement.style.display='block';
    }
    else if(displayVal == 'block'){
        divElement.style.display='none';
    }
    if(arrowElement.className == "arrow right"){
        arrowElement.className = "arrow down";
    }
    else if(arrowElement.className == "arrow down"){
        arrowElement.className = "arrow right";
    }
}

let jobArrow = document.getElementById('jobArrow');
let jobDiv = document.getElementById('jobAnalyzerDiv');

jobArrow.addEventListener("click", () => {showText(jobDiv, jobArrow)});

let chatArrow = document.getElementById('chatArrow');
let chatDiv = document.getElementById('chatServerDiv');

chatArrow.addEventListener("click", () => {showText(chatDiv, chatArrow)});

let DNSArrow = document.getElementById('DNSArrow');
let DNSDiv = document.getElementById('DNSResolverDiv');

DNSArrow.addEventListener("click", () => {showText(DNSDiv, DNSArrow)});

let sharksArrow = document.getElementById('sharksArrow');
let sharksDiv = document.getElementById('sharksAndMinnowsDiv');

sharksArrow.addEventListener("click", () => {showText(sharksDiv, sharksArrow)});

let mazeArrow = document.getElementById('mazeArrow');
let mazeDiv = document.getElementById('mazeDiv');

mazeArrow.addEventListener("click", () => {showText(mazeDiv, mazeArrow)});

let MSDArrow = document.getElementById('MSDArrow');
let MSDDiv = document.getElementById('MSDScriptDiv');

MSDArrow.addEventListener("click", () => {showText(MSDDiv, MSDArrow)});

let lifeArrow = document.getElementById('lifeArrow');
let lifeDiv = document.getElementById('lifeStyleDiv');

lifeArrow.addEventListener("click", () => {showText(lifeDiv, lifeArrow)});

let LMSArrow = document.getElementById('LMSArrow');
let LMSDiv = document.getElementById('LMSDiv');

LMSArrow.addEventListener("click", () => {showText(LMSDiv, LMSArrow)});

