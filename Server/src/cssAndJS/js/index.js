'use strict';
const elem = {};
let questionBox = document.getElementById("questionBox");

window.onload = async() =>{
        // Replace ./data.json with your JSON feed
    fetch('./data/questionAnswers.json').then(response => {
      return response.json();
    }).then(data => {
        elem.data = data;
      console.log(data);
      printQuestions(data);
    }).catch(err => {
      console.error(err.stack)
    });
}

function printQuestions(data){
    for(let i = 1; i <= 10; i++){
        let lvlTitle = document.createElement('h2');
        lvlTitle.className = 'lvlTitle';
        lvlTitle.textContent = "Level "+i;
        questionBox.appendChild(lvlTitle);
        // Creation of the seperate level questions
        let questionLevel = document.createElement('div');
        questionLevel.className = 'Level';
        questionLevel.id = 'level'+i;
        questionBox.appendChild(questionLevel);

// Loop to go through the different questions in the level
      defineJsonPath(data, i);

        for(let x = 0; x < elem.jsonPath.length; x++){
            let question = document.createElement('div');
            question.className = 'question';
            question.id =  x;
            questionLevel.appendChild(question);

// putting the questiion in the right tag
            let q = document.createElement('p');
            q.className = 'q';
            let text = elem.jsonPath[x].question;
            q.textContent = text;
            question.appendChild(q);

            let deleteBtn = document.createElement('button');
            deleteBtn.className = 'deleteBtn';
            deleteBtn.id = 'delBtn';
            deleteBtn.textContent = 'Delete Question'
            deleteBtn.name = i;
            deleteBtn.value = x;
            question.appendChild(deleteBtn);

            deleteBtn.addEventListener('click', async() => {
                console.log(i);
                console.log(x);

               defineJsonPath(data, i);
               console.log(elem.jsonPath[x]);
               elem.jsonPath.splice(x, 1);
               console.log(data);

               const send = await fetch('../api/storeResults', {
                 method: 'POST',
                 headers: {'Content-Type': 'application/json'},
                 body: JSON.stringify(data),
               });
               const res = await send.json();
               if(res.success === true){
                 console.log('saved');
               } else{console.log('error');}

            });
        }
    }

}

function defineJsonPath(data, i){
    if( i === 1){elem.jsonPath = data.Lvl1;}
    else if (i === 2){elem.jsonPath = data.Lvl2;}
    else if (i === 3){elem.jsonPath = data.Lvl3;}
    else if (i === 4){elem.jsonPath = data.Lvl4;}
    else if (i === 5){elem.jsonPath = data.Lvl5;}
    else if (i === 6){elem.jsonPath = data.Lvl6;}
    else if (i === 7){elem.jsonPath = data.Lvl7;}
    else if (i === 8){elem.jsonPath = data.Lvl8;}
    else if (i === 9){elem.jsonPath = data.Lvl9;}
    else if (i === 10){elem.jsonPath = data.Lvl10;}

    return elem.jsonPath
}

//download button for the responses
let downloadButton = document.getElementById('downloadButton').addEventListener('click', function(){
   console.log('Download Button clicked');

   // Start the download of yournewfile.txt file with the content from the text area
       let text = JSON.stringify(elem.data);
       let filename = "CodeTracerQuestionList.json";
       download(filename, text);
   }, false);
function download(filename, text){
    let element = document.createElement('a');
    element.setAttribute('href', 'data:json/plain;charset=utf-8,' + encodeURIComponent(JSON.stringify(text)));
    element.setAttribute('download', filename);
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
}
