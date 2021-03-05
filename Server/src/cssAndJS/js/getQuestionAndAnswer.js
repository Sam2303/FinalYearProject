const submitButton = document.getElementById('submitBtn');
submitButton.addEventListener('click', async() => {

    let question = document.getElementById("question").value;
    console.log(question);


    let forUpload = {
        "question" : question
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

});
