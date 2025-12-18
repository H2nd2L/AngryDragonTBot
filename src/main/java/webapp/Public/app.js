const tg = window.Telegram?.WebApp;

if (tg) {
  tg.expand(); // разворачивает на весь экран
  tg.enableClosingConfirmation(); // подтверждение закрытия
  document.body.style.backgroundColor =
    tg.themeParams.bg_color || '#21274f';
} else {
  console.log('Запущено вне Telegram');
}

const canvas = document.getElementById('snow');
const ctx = canvas.getContext('2d');

function resizeCanvas() {
  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;
}
resizeCanvas();
window.addEventListener('resize', resizeCanvas);


const flakes = [];
const FLAKES_COUNT = 200;

for (let i = 0; i < FLAKES_COUNT; i++) {
  flakes.push({
    x: Math.random() * canvas.width,
    y: Math.random() * canvas.height,
    r: Math.random() * 1 + 1,
    speed: Math.random() * 1 + 0.5
  });
}

function drawSnow() {
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  ctx.fillStyle = 'white';

  ctx.beginPath();
  for (const f of flakes) {
    ctx.moveTo(f.x, f.y);
    ctx.arc(f.x, f.y, f.r, 0, Math.PI * 2);
  }
  ctx.fill();

  moveSnow();
  requestAnimationFrame(drawSnow);
}

function moveSnow() {
  for (const f of flakes) {
    f.y += f.speed;

    if (f.y > canvas.height) {
      f.y = -f.r;
      f.x = Math.random() * canvas.width;
    }
  }
}
drawSnow();

const userId = 1;

document.getElementById('bandit-btn').onclick = () => {
    fetch('/game/bandit/spin', {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify({ userId })
    })
    .then(r => r.json())
    .then(data => {
        document.getElementById('output').textContent = JSON.stringify(data, null, 2)
    });
};