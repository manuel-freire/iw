document.addEventListener("DOMContentLoaded", function () {
  const themeToggle = document.getElementById("themeToggle");
  const themeIcon = document.getElementById("themeIcon");
  const themeText = document.getElementById("themeText");
  const htmlElement = document.documentElement;

  const currentTheme = htmlElement.getAttribute("data-bs-theme") || "light";
  updateButton(currentTheme);

  if (!themeToggle) return;

  themeToggle.addEventListener("click", function () {
    const current = htmlElement.getAttribute("data-bs-theme") || "light";
    const next = current === "light" ? "dark" : "light";

    htmlElement.setAttribute("data-bs-theme", next);
    localStorage.setItem("theme", next);
    updateButton(next);
  });

  function updateButton(theme) {
    if (!themeIcon || !themeText) return;

    if (theme === "dark") {
      themeIcon.classList.remove("bi-moon");
      themeIcon.classList.add("bi-sun");
      themeText.textContent = "Light";
      themeToggle.classList.add("btn-dark-theme");
      themeToggle.classList.remove("btn-light-theme");
    } else {
      themeIcon.classList.remove("bi-sun");
      themeIcon.classList.add("bi-moon");
      themeText.textContent = "Dark";
      themeToggle.classList.remove("btn-dark-theme");
     themeToggle.classList.add("btn-light-theme");
    }
  }
});
