document.addEventListener('DOMContentLoaded', () => {
  const input = document.getElementById('wordleInput');
  const btn = document.getElementById('wordleBtn');
  const board = document.getElementById('wordleBoard');
  const popup = document.getElementById('popup');
  const popupContent = document.getElementById('popupContent');
  const popupBtn = document.getElementById('popupBtn');
  const letterHint = document.getElementById('letterHint');

  const words = ['дракон','огонь','крыло','золото','пламя','пятно','актер','башня','ведро','басня','резня','звено','котел','мафия','бекон','берег','бисер','брошь','буфет','ветвь','выбор','город','дебют','дзюдо','досье','жених'];
  let secret = '';
  let tries = 0;
  const mainBtn = document.getElementById('mainBtn');

  mainBtn.addEventListener('click', () => {
    // Перенаправление на главную страницу
    window.location.href = 'index.html';
  });


  function start() {
    secret = words[Math.floor(Math.random()*words.length)];
    tries = 0;
    board.textContent = '';
    input.disabled = false;
    btn.disabled = false;
    letterHint.textContent = `Введите ровно ${secret.length} букв`;
  }

  function showPopup(message) {
    popupContent.innerHTML = message;
    popup.classList.remove('hidden');
  }

  popupBtn.addEventListener('click', () => {
    popup.classList.add('hidden');
    start();
  });

  btn.addEventListener('click', () => {
    const guess = input.value.toLowerCase();

    // Проверка длины слова
    if (guess.length !== secret.length) {
      letterHint.textContent = `Слово должно быть ровно ${secret.length} букв!`;
      return;
    }

    let result = '';
    for (let i=0;i<secret.length;i++) {
      if (guess[i] === secret[i]) result += '🟩';
      else if (secret.includes(guess[i])) result += '🟨';
      else result += '⬜';
    }

    board.textContent += guess + ' ' + result + '\n';
    tries++;

    if (guess === secret) {
      input.disabled = true;
      btn.disabled = true;
      showPopup(`🎉 Поздравляем! Вы угадали слово: ${secret}`);
    } else if (tries >= 6) {
      input.disabled = true;
      btn.disabled = true;
      showPopup(`😢 Не повезло! Слово было: ${secret}`);
    } else {
      letterHint.textContent = `Введите ровно ${secret.length} букв`;
    }

    input.value = '';
    input.focus();

  });

  start();
});
