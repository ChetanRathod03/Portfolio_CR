(function(){
  // ---------- preloader ----------
  const bootMsgs = ["> initializing_", "> loading modules...", "> connecting to database...", "> starting spring-boot context...", "> ready."];
  let bi = 0;
  const bootLine = document.getElementById('bootLine');
  const bootInterval = setInterval(()=>{
    bi++;
    if(bi < bootMsgs.length){ bootLine.innerHTML = bootMsgs[bi]; }
  }, 300);
  window.addEventListener('load', ()=>{
    setTimeout(()=>{
      clearInterval(bootInterval);
      document.getElementById('preloader').classList.add('hide');
    }, 1500);
  });

  // ---------- custom cursor ----------
  const dot = document.getElementById('cursorDot');
  const ring = document.getElementById('cursorRing');
  let mx=0,my=0, rx=0, ry=0;
  window.addEventListener('mousemove', e=>{
    mx = e.clientX; my = e.clientY;
    dot.style.left = mx+'px'; dot.style.top = my+'px';
  });
  function animRing(){
    rx += (mx-rx)*0.18; ry += (my-ry)*0.18;
    ring.style.left = rx+'px'; ring.style.top = ry+'px';
    requestAnimationFrame(animRing);
  }
  animRing();
  document.querySelectorAll('a,button,.card,.proj-card,.skill-pill,input,textarea,select').forEach(el=>{
    el.addEventListener('mouseenter', ()=>ring.classList.add('hover'));
    el.addEventListener('mouseleave', ()=>ring.classList.remove('hover'));
  });

  // ---------- header scroll state ----------
  const header = document.getElementById('siteHeader');
  const backToTop = document.getElementById('backToTop');
  window.addEventListener('scroll', ()=>{
    header.classList.toggle('scrolled', window.scrollY > 20);
    backToTop.classList.toggle('show', window.scrollY > 500);
  });
  backToTop.addEventListener('click', ()=>window.scrollTo({top:0,behavior:'smooth'}));

  // ---------- mobile menu ----------
  const burger = document.getElementById('burgerBtn');
  const mobileMenu = document.getElementById('mobileMenu');
  burger.addEventListener('click', ()=>mobileMenu.classList.toggle('open'));
  mobileMenu.querySelectorAll('a').forEach(a=>a.addEventListener('click', ()=>mobileMenu.classList.remove('open')));

  // ---------- typed role ----------
  const roles = ["Java Backend Engineer","Spring Boot Developer","Cloud & DevOps Enthusiast","Full-Stack Builder"];
  const typedEl = document.getElementById('typedRole');
  let ri=0, ci=0, deleting=false;
  function typeLoop(){
    const current = roles[ri];
    if(!deleting){
      ci++;
      typedEl.textContent = current.slice(0,ci);
      if(ci === current.length){ deleting = true; setTimeout(typeLoop, 1400); return; }
    } else {
      ci--;
      typedEl.textContent = current.slice(0,ci);
      if(ci === 0){ deleting = false; ri = (ri+1)%roles.length; }
    }
    setTimeout(typeLoop, deleting ? 35 : 65);
  }
  setTimeout(typeLoop, 1800);

  // ---------- reveal on scroll ----------
  const io = new IntersectionObserver((entries)=>{
    entries.forEach(e=>{ if(e.isIntersecting){ e.target.classList.add('in'); io.unobserve(e.target); } });
  }, {threshold:0.15});
  document.querySelectorAll('.reveal').forEach(el=>io.observe(el));

  // ---------- tech stack data ----------
  const skills = [
    {n:'Java (Core)', c:'lang'}, {n:'Python', c:'lang'}, {n:'JavaScript', c:'lang'}, {n:'HTML5', c:'lang'}, {n:'CSS3', c:'lang'},
    {n:'React.js', c:'front'}, {n:'Tailwind CSS', c:'front'}, {n:'HTML', c:'front'}, {n:'CSS', c:'front'},
    {n:'Spring Boot', c:'back'}, {n:'Spring MVC', c:'back'}, {n:'Spring Security', c:'back'}, {n:'Hibernate', c:'back'}, {n:'REST APIs', c:'back'},
    {n:'PostgreSQL', c:'db'}, {n:'MySQL', c:'db'}, {n:'MongoDB', c:'db'},
    {n:'AWS EC2', c:'cloud'}, {n:'AWS S3', c:'cloud'}, {n:'AWS RDS', c:'cloud'}, {n:'Docker', c:'cloud'},
    {n:'Git', c:'tools'}, {n:'GitHub', c:'tools'}, {n:'Postman', c:'tools'}, {n:'JWT', c:'tools'}, {n:'OAuth 2.0', c:'tools'}, {n:'RBAC', c:'tools'},
  ];
  const skillGrid = document.getElementById('skillGrid');
  function renderSkills(cat){
    skillGrid.innerHTML = '';
    skills.filter(s => cat==='all' || s.c===cat).forEach(s=>{
      const el = document.createElement('div');
      el.className = 'skill-pill';
      el.innerHTML = '<span class="dot2"></span>'+s.n;
      skillGrid.appendChild(el);
    });
  }
  renderSkills('all');
  document.getElementById('tabRow').addEventListener('click', e=>{
    if(e.target.classList.contains('tab')){
      document.querySelectorAll('#tabRow .tab').forEach(t=>t.classList.remove('active'));
      e.target.classList.add('active');
      renderSkills(e.target.dataset.cat);
    }
  });

  // ---------- project filter ----------
  document.getElementById('filterRow').addEventListener('click', e=>{
    if(e.target.classList.contains('tab')){
      document.querySelectorAll('#filterRow .tab').forEach(t=>t.classList.remove('active'));
      e.target.classList.add('active');
      const f = e.target.dataset.filter;
      document.querySelectorAll('#projGrid .proj-card').forEach(card=>{
        const show = f==='all' || card.dataset.filter===f || card.dataset.filter==='all';
        card.classList.toggle('hidden-card', !show);
      });
    }
  });

  // ---------- contact form: wired to the Spring Boot backend ----------
  // Set window.PORTFOLIO_API_BASE before this script runs (e.g. in a <script> tag
  // above, or by editing the default below) to point at your deployed API.
  // Defaults to same-origin '/api', which is correct once this file is served
  // behind the Nginx config from the backend repo (it proxies /api/* to Spring Boot).
  const API_BASE = window.PORTFOLIO_API_BASE || '/api';

  const contactForm = document.getElementById('contactForm');
  const formStatus = document.getElementById('formStatus');
  const sendBtn = document.getElementById('sendMsgBtn');

  function setFieldError(fieldEl, msg){
    const wrap = fieldEl.closest('.field');
    if(!wrap) return;
    wrap.classList.add('field-error');
    let msgEl = wrap.querySelector('.field-error-msg');
    if(!msgEl){
      msgEl = document.createElement('div');
      msgEl.className = 'field-error-msg';
      wrap.appendChild(msgEl);
    }
    msgEl.textContent = msg;
  }
  function clearFieldErrors(){
    contactForm.querySelectorAll('.field-error').forEach(w=>{
      w.classList.remove('field-error');
      const m = w.querySelector('.field-error-msg');
      if(m) m.remove();
    });
  }

  contactForm.addEventListener('submit', async (e)=>{
    e.preventDefault();
    clearFieldErrors();
    formStatus.style.color = 'var(--text-2)';

    const nameEl = document.getElementById('f-name');
    const emailEl = document.getElementById('f-email');
    const messageEl = document.getElementById('f-message');

    // light client-side check before hitting the network
    let hasError = false;
    if(!nameEl.value.trim()){ setFieldError(nameEl, 'Name is required'); hasError = true; }
    if(!emailEl.value.trim()){ setFieldError(emailEl, 'Email is required'); hasError = true; }
    if(!messageEl.value.trim()){ setFieldError(messageEl, 'Message is required'); hasError = true; }
    if(hasError){ formStatus.textContent = 'Please fix the highlighted fields.'; formStatus.style.color = '#ff8787'; return; }

    const payload = {
      name: nameEl.value.trim(),
      email: emailEl.value.trim(),
      company: document.getElementById('f-company').value.trim(),
      designation: document.getElementById('f-designation').value.trim(),
      phone: document.getElementById('f-phone').value.trim(),
      subject: document.getElementById('f-subject').value.trim(),
      message: messageEl.value.trim(),
      resumeRequested: document.getElementById('f-resume').checked,
      scheduleInterviewRequested: document.getElementById('f-interview').checked,
      priority: document.getElementById('f-priority').value,
      website: document.getElementById('f-website').value // honeypot
    };

    const originalLabel = sendBtn.textContent;
    sendBtn.disabled = true;
    sendBtn.textContent = 'Sending…';
    formStatus.textContent = '';

    try {
      const res = await fetch(`${API_BASE}/contact`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      const data = await res.json().catch(()=>null);

      if(res.ok && data && data.success){
        formStatus.style.color = '#5fd97a';
        formStatus.textContent = data.message || "Thanks — I'll get back to you soon.";
        contactForm.reset();
      } else if(res.status === 400 && data && data.data){
        // field-level validation errors from the backend, e.g. { name: "Name is required" }
        Object.entries(data.data).forEach(([field, msg])=>{
          const el = document.getElementById('f-' + field);
          if(el) setFieldError(el, msg); else formStatus.textContent = msg;
        });
        formStatus.style.color = '#ff8787';
        formStatus.textContent = 'Please fix the highlighted fields.';
      } else {
        formStatus.style.color = '#ff8787';
        formStatus.textContent = (data && data.message) || 'Something went wrong — please email me directly instead.';
      }
    } catch (err) {
      // Most likely cause in this preview: no backend is deployed at API_BASE yet.
      formStatus.style.color = '#ffb648';
      formStatus.textContent = "Couldn't reach the server — the backend may not be deployed yet. Email me directly at chetanrathod71252@gmail.com in the meantime.";
    } finally {
      sendBtn.disabled = false;
      sendBtn.textContent = originalLabel;
    }
  });

  document.getElementById('downloadResumeBtn').addEventListener('click', (e)=>{
    e.preventDefault();
  });

  // ---------- background canvas: connected particles ----------
  const canvas = document.getElementById('bgCanvas');
  const ctx = canvas.getContext('2d');
  let w,h,particles=[];
  function resize(){
    w = canvas.width = window.innerWidth;
    h = canvas.height = window.innerHeight;
  }
  window.addEventListener('resize', resize);
  resize();
  const count = window.innerWidth < 700 ? 35 : 70;
  for(let i=0;i<count;i++){
    particles.push({
      x: Math.random()*w, y: Math.random()*h,
      vx: (Math.random()-0.5)*0.3, vy: (Math.random()-0.5)*0.3,
      r: Math.random()*1.6+0.6
    });
  }
  function draw(){
    ctx.clearRect(0,0,w,h);
    for(let i=0;i<particles.length;i++){
      const p = particles[i];
      p.x += p.vx; p.y += p.vy;
      if(p.x<0||p.x>w) p.vx*=-1;
      if(p.y<0||p.y>h) p.vy*=-1;
      ctx.beginPath();
      ctx.arc(p.x,p.y,p.r,0,Math.PI*2);
      ctx.fillStyle = 'rgba(122,162,255,0.55)';
      ctx.fill();
      for(let j=i+1;j<particles.length;j++){
        const q = particles[j];
        const dx=p.x-q.x, dy=p.y-q.y, dist=Math.sqrt(dx*dx+dy*dy);
        if(dist < 130){
          ctx.beginPath();
          ctx.moveTo(p.x,p.y); ctx.lineTo(q.x,q.y);
          ctx.strokeStyle = 'rgba(122,162,255,'+(0.12*(1-dist/130))+')';
          ctx.lineWidth = 1;
          ctx.stroke();
        }
      }
    }
    requestAnimationFrame(draw);
  }
  draw();
})();


