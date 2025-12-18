document.addEventListener('DOMContentLoaded', () => {
  const cells = document.querySelectorAll('#ttt .cell');
  const popup = document.getElementById('popup');
  const popupContent = document.getElementById('popupContent');
  const popupBtn = document.getElementById('popupBtn');

  let board = Array(9).fill(null);
  const player = '❌';
  const computer = '⭕';
const mainBtn = document.getElementById('mainBtn');

  mainBtn.addEventListener('click', () => {
    // Перенаправление на главную страницу
    window.location.href = 'index.html';
  });

  function checkWin(p) {
    const wins = [
      [0,1,2],[3,4,5],[6,7,8],
      [0,3,6],[1,4,7],[2,5,8],
      [0,4,8],[2,4,6]
    ];
    return wins.some(w => w.every(i => board[i] === p));
  }

  function draw() {
    cells.forEach((cell, i) => cell.textContent = board[i] || '');
  }

  function showPopup(message) {
    popupContent.textContent = message;
    popup.classList.remove('hidden');
  }

  function computerMove() {
    const empty = board.map((v,i)=>v===null?i:null).filter(v=>v!==null);
    if (!empty.length) return;
    const choice = empty[Math.floor(Math.random()*empty.length)];
    board[choice] = computer;
    draw();

    if (checkWin(computer)) showPopup('💻 Компьютер победил!');
    else if (board.every(c=>c)) showPopup('🤝 Ничья');
  }

  cells.forEach(cell => {
    cell.addEventListener('click', () => {
      const i = Number(cell.dataset.i);
      if (board[i] || !popup.classList.contains('hidden')) return;

      board[i] = player;
      draw();

      if (checkWin(player)) { showPopup('🎉 Вы победили!'); return; }
      if (board.every(c=>c)) { showPopup('🤝 Ничья'); return; }

      setTimeout(computerMove, 400);
    });
  });

  popupBtn.addEventListener('click', () => {
    popup.classList.add('hidden');
    board.fill(null);
    draw();
  });
});
