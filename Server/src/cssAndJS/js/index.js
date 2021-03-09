const testBtn = document.getElementById('testBtn');

testBtn.addEventListener('click', async() => {

    let json = {
        "userName" : '889823',
        "password" : 'password1'
    }

    const send = await fetch('../api/getLogin', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(json),
    });
    const res = await send.json();
    if(res.success === true){
      console.log('saved');
    } else{console.log('error');}

});
