const submitButton = document.getElementById('submitBtn');
get_random = function (list) {
  return list[Math.floor((Math.random()*list.length))];
}
// function to allow tabbing in the question input box
document.getElementById('question').addEventListener('keydown', function(e) {
  if (e.key == 'Tab') {
    e.preventDefault();
    var start = this.selectionStart;
    var end = this.selectionEnd;

    // set textarea value to: text before caret + tab + text after caret
    this.value = this.value.substring(0, start) +
      "\t" + this.value.substring(end);

    // put caret at right position again
    this.selectionStart =
      this.selectionEnd = start + 1;
  }
});

submitButton.addEventListener('click', async() => {

    let rawQuestion = document.getElementById("question").value;
    console.log(rawQuestion);



    //check between here if the python code will run, if not give error message to inputter

//  Do replace here
    let oneToTen = [1,2,3,4,5,6,7,8,9,10];
    let tenToTwenty = [10,11,12,13,14,15,16,17,18,19,20];
    let zeroToTwenty = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20];
    let zeroToThree = [0,1,2,3];
    let wordList = ["word", "portsmouth", "second"];

    let question = rawQuestion.replace("£", get_random(oneToTen));
    question = question.replace("$", get_random(tenToTwenty));
    question = question.replace("&", get_random(zeroToTwenty));
    question = question.replace("!", get_random(zeroToTwenty));
    question = question.replace("?", get_random(zeroToThree));
    question = question.replace("~", get_random(wordList));
    console.log(question);

    // post request to test if the question runs
    let testQuestion = {
        'question' : question
    };

    const send = await fetch('../api/testPython', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(testQuestion),
    });
    const res = await send.json();
    if(res.success === true){
      console.log('pyhton file ran');
      window.alert("Question has been saved")

    let forUpload = {
        "question" : rawQuestion
    };
    console.log(forUpload);


    const file = await fetch("./data/questionAnswers.json")
    const json = await file.json();
    console.log(json);

//------------ GET WHICH LEVEL TO ADD TO! -----------

    let amountOfOptions = document.getElementsByName('radioCheckBoxes');
    let checked = '';
    for(let z = 0; z < amountOfOptions.length; z++){
    if(amountOfOptions[z].checked){
        checked = amountOfOptions[z].value;
    }
    else{amountOfOptions[z].checked = false;}
    }

    if(checked === 'Lvl1'){
        json.Lvl1.push(forUpload);
    }else if (checked === 'Lvl2') {
        json.Lvl2.push(forUpload);
    }else if (checked === 'Lvl3') {
        json.Lvl3.push(forUpload);
    }else if (checked === 'Lvl4') {
        json.Lvl4.push(forUpload);
    }else if (checked === 'Lvl5') {
        json.Lvl5.push(forUpload);
    }else if (checked === 'Lvl6') {
        json.Lvl6.push(forUpload);
    }else if (checked === 'Lvl7') {
        json.Lvl7.push(forUpload);
    }else if (checked === 'Lvl8') {
        json.Lvl8.push(forUpload);
    }else if (checked === 'Lvl9') {
        json.Lvl9.push(forUpload);
    }else if (checked === 'Lvl10') {
        json.Lvl10.push(forUpload);
    }else{console.log('AN ERROR HAS OCCURED');}

   // --------------------------------------------------
    console.log(json);



    const send = await fetch('../api/storeResults', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(json),
    });
    const res = await send.json();
    if(res.success === true){
      console.log('saved');
    } else{console.log('error');}

    // Clear the text boxes
    let questionBox = document.getElementById('question');
    let answerBox = document.getElementById('answer');

    questionBox.value = '';
    } else{
        console.log('error');
        window.alert("THIS IS NOT A VALID PYHTON QUESTION, EDIT IT AND TRY AGAIN");
     }

});
