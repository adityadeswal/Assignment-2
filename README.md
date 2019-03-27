# Top Pack
TopPack is Application for listing top 10 popular JavaScript packages 
1. Used okhttp to make REST calls.
2. Implemented method called search, which takes a keyword string as a parameter, that searches public GitHub repository by keywords and lists them.
3. Implemented method called import, which takes ‘GitHub Repository id’ as an input, that searches for package.json file in the root folder of the git repo. If the file exists, output all the packages as comma separated string that are used in the repository.
4. Implemented method toppacks, which lists the top 10 packages which have been sourced (all the results which we will be fetching through “import”
method till now) with number of repositories (we have imported) using those packages.
