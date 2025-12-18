document.addEventListener('DOMContentLoaded', () => {
  const words = ['дракон','огонь','крыло','золото','пламя','пятно','актер','башня','ведро','басня','резня','звено','котел','мафия','бекон','берег','бисер','брошь','буфет','ветвь','выбор','город','дебют','дзюдо','досье','жених'];
  const alphabet = 'абвгдеёжзийклмнопрстуфхцчшщьыэюя'.split('');

  const wordDiv = document.getElementById('hangmanWord');
  const lettersDiv = document.getElementById('letters');
  const popup = document.getElementById('popup');
  const popupText = document.getElementById('popupText');
  const popupBtn = document.getElementById('popupBtn');
  const parts = ['head','body','arm-left','arm-right','leg-left','leg-right'];

  let word = '';
  let guessed = [];
  let errors = 0;

  function start() {
    word = words[Math.floor(Math.random()*words.length)];
    guessed = [];
    errors = 0;
    drawWord();
    drawLetters();
    parts.forEach(p => document.querySelector('.' + p).style.opacity = 0);
    popup.style.display = 'none';
  }

  function drawWord() {
    wordDiv.textContent = word.split('').map(l => guessed.includes(l) ? l : '_').join(' ');
  }

  function drawLetters() {
    lettersDiv.innerHTML = '';
    for (let c of alphabet) {
      const btn = document.createElement('button');
      btn.textContent = c;
      btn.disabled = guessed.includes(c);
      btn.onclick = () => guess(c, btn);
      lettersDiv.appendChild(btn);
    }
  }

  function guess(letter, btn) {
    if (guessed.includes(letter)) return;
    guessed.push(letter);
    btn.disabled = true;

    if (!word.includes(letter)) {
      errors++;
      if (errors <= parts.length) {
        document.querySelector('.' + parts[errors-1]).style.opacity = 1;
      }
    }

    drawWord();

    if (word.split('').every(l => guessed.includes(l))) {
      showWin();
    } else if (errors >= parts.length) {
      showLose();
    }
  }

  function showWin() {
    popupText.textContent = `🎉 Поздравляем! Слово: ${word}`;
    popup.style.display = 'flex';
    launchFireworks();
  }

  function showLose() {
    popupText.textContent = `😢 Вы проиграли. Слово: ${word}`;
    popup.style.display = 'flex';
    lettersDiv.querySelectorAll('button').forEach(b=>b.disabled=true);
    showFalling();
  }

  popupBtn.addEventListener('click', start);

  function launchFireworks() {
    for(let i=0;i<20;i++){
      const f = document.createElement('div');
      f.className='firework';
      f.style.left = (Math.random()*window.innerWidth)+'px';
      f.style.top = (Math.random()*window.innerHeight/2)+'px';
      f.style.background = `hsl(${Math.random()*360},100%,50%)`;
      f.style.setProperty('--x', (Math.random()*200-100)+'px');
      f.style.setProperty('--y', (Math.random()*200-100)+'px');
      document.body.appendChild(f);
      setTimeout(()=>f.remove(),1000);
    }
  }

  function showFalling() {
    const fallen = document.createElement('div');
    fallen.textContent = '💀';
    fallen.style.position='fixed';
    fallen.style.left = Math.random()*window.innerWidth+'px';
    fallen.style.top = '-50px';
    fallen.className='fall';
    document.body.appendChild(fallen);
    setTimeout(()=>fallen.remove(),1500);
  }

  // Снежинки
  const canvas = document.getElementById('snow');
  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;
  const ctx = canvas.getContext('2d');
  const flakes = [];
  for(let i=0;i<100;i++){
    flakes.push({x:Math.random()*canvas.width,y:Math.random()*canvas.height,r:Math.random()*3+1,speed: Math.random()*1+0.5});
  }

  function drawSnow(){
    ctx.clearRect(0,0,canvas.width,canvas.height);
    flakes.forEach(f=>{
      ctx.beginPath();
      ctx.arc(f.x,f.y,f.r,0,Math.PI*2);
      ctx.fillStyle="#fff";
      ctx.fill();
      f.y+=f.speed;
      if(f.y>canvas.height){f.y=0; f.x=Math.random()*canvas.width;}
    });
    requestAnimationFrame(drawSnow);
  }
  drawSnow();

  start();
});
