# 🌞 MAXXEnergyV2 💡

**MAXXEnergyV2** is the **remastered and restructured version** of the MAXX-Energy project.  
This edition was created by **consolidating and refining the previous version**, with all input gathered from the team. The result is a **simplistic structure** 🧩 and **clean formatting** 🧹 that make the project easier to navigate, maintain, and extend.  

---

## 📂 Project Structure  

This minimal and organized layout was intentionally designed to keep everything in one place while reducing clutter. 
My focus was on readability and cleanliness to optimzie future changes and easier adaptability.  

---

## 🚀 Getting Started  

Clone the repository:  

```bash
git clone https://github.com/frederickdlanejr/MAXXEnergyV2.git
cd MAXXEnergyV2
🌐 About Page (Static Website)

Open index.html directly in your browser, or run a quick local server:

cd about-page
python -m http.server 8080

☕ SolarData (Java Service)

Run the backend service using Maven:

cd solardata
mvn spring-boot:run

🔄 Updating Subtrees

Each subproject can still be updated individually as needed.

About Page:

git fetch about
git subtree pull --prefix=about-page about main


SolarData:

git fetch solardata
git subtree pull --prefix=solardata solardata main

🤝 Contributing

This version was built from the ground up by collecting, consolidating, and refining all contributions from the team.
To keep everything consistent going forward:

🌱 Create a feature branch

git checkout -b feature-name


✏️ Make your updates and commit

📬 Open a Pull Request for team review

🏗️ Project History

✅ MAXX-Energy (V1): Original project with separate structures

🔄 MAXXEnergyV2: Consolidated and remastered for a simpler, cleaner format

📖 This README reflects the new streamlined approach, ensuring clarity for both new and returning contributors
