document.addEventListener('DOMContentLoaded', () => {
  const reels = document.querySelectorAll('#reels .reel');
  const btn = document.getElementById('spinBtn');
  const popup = document.getElementById('popup');
  const popupContent = document.getElementById('popupContent');
  const popupBtn = document.getElementById('popupBtn');

  const symbols = ['🐲','💎','🔥','🍖','⭐'];
  let spinningIntervals = [null, null, null];
const mainBtn = document.getElementById('mainBtn');

  mainBtn.addEventListener('click', () => {
    // Перенаправление на главную страницу
    window.location.href = 'index.html';
  });

  function randomSymbol() {
    return symbols[Math.floor(Math.random() * symbols.length)];
  }

  function showPopup(message, isWin=false) {
    popupContent.innerHTML = message + (isWin ? ' 🎆🎉' : ' 😢💔');
    popup.classList.remove('hidden');
  }

  popupBtn.addEventListener('click', () => {
    popup.classList.add('hidden');
    btn.disabled = false;
    btn.textContent = 'Играть';
  });

  btn.addEventListener('click', () => {
    if (spinningIntervals.some(i => i !== null)) return;

    btn.disabled = true;
    btn.textContent = 'Крутим...';

    // решаем заранее, будет ли выигрыш
    const win = Math.random() < 0.25;
    const finalSymbols = win
      ? Array(3).fill(symbols[Math.floor(Math.random() * symbols.length)])
      : [randomSymbol(), randomSymbol(), randomSymbol()];

    // запускаем анимацию вращения
    reels.forEach((r, i) => {
      spinningIntervals[i] = setInterval(() => {
        r.textContent = randomSymbol();
      }, 80 + i*40);
    });

    // остановка через 2 секунды
    setTimeout(() => {
      spinningIntervals.forEach((int, i) => {
        clearInterval(int);
        reels[i].textContent = finalSymbols[i];
        spinningIntervals[i] = null;
      });

      if (win) {
        showPopup(`🎉 Поздравляем! Вы выиграли!`, true);
      } else {
        showPopup('😢 Не повезло... Попробуйте ещё раз', false);
      }

      btn.textContent = 'Играть ещё раз';
    }, 2000);
  });
});
