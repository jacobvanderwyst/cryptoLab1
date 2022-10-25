import os

path="/var/www/html/"
filename="server"
os.system("sudo rm -rf /var/www/html/cryptoLab1")
os.system("sudo rm -f {path}sources.txt")
os.system("echo \"removed old\"")
os.system("cd {path} && git clone https://github.com/jacobvanderwyst/cryptoLab1.git")
os.system("find -name \"*.java\" > sources.txt")
os.system("javac @sources.txt")
os.system("echo \"compiled classes\"")

os.system("tree")
