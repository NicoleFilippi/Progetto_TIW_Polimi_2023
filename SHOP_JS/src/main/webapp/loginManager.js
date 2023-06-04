/**
 * elementi login
 */

function LoginScreen(orch){
	this.orchestrator = orch;
	
	this.loginForm = document.getElementById("login_form");
	this.alertContainer = document.getElementById("login_alert");
	this.button = document.getElementById("login_signup_button");
	this.elements = document.getElementById("login_elements");
	
	let self = this;
	
	//bottone login
	
 	this.loginForm.addEventListener("submit", function(e) {
		e.preventDefault();
		if (self.loginForm.checkValidity()) {				
			makeCall("POST", "CheckLogin", self.loginForm, function(req) {
            	if (req.readyState == XMLHttpRequest.DONE) {
            		let response = req.responseText;
	        		if (req.status === 200) {
						sessionStorage.setItem("user", response);	
						sessionStorage.setItem("cart",JSON.stringify(new Cart()));						
	           			window.location.href="home.html";
	        		} else {
						if (req.status === 401){
							makeCall("GET", "GetUser", null, function(req){
								if (req.readyState == XMLHttpRequest.DONE) {
				            		let response = req.responseText;
					        		if (req.status === 200) {
										sessionStorage.setItem("user", response);
										sessionStorage.setItem("cart",JSON.stringify(new Cart()));						
					           			window.location.href="home.html";
					        		} else {
					           			self.alertContainer.textContent = response;
					           			self.alertContainer.hidden = false;
					          		}
				      			}
				      		});
			      		}else{
							self.alertContainer.textContent = response;
	           				self.alertContainer.hidden = false;
						}
	          		}
      			}
      		});
		} else {
    		self.loginForm.reportValidity();
  		}
	});
	
	//bottone signup
	
	this.button.addEventListener("click", function(e){
		self.orchestrator.goToSignup();
	});
	
	//caricamento
	
	this.load = function(){
		this.elements.hidden = false;
	}
	
	//reset
	
	this.clear = function(){
		this.elements.hidden = true;
		this.loginForm.reset();
		this.alertContainer.textContent = "";
		this.alertContainer.hidden = true;
	}
	
	this.clear();
}


/**
 * elementi signup
 */

function SignupScreen(orch){
	this.orchestrator = orch;
	
	this.wizard = [ document.getElementById("signup_w1") , document.getElementById("signup_w2") ];
	this.form = document.getElementById("signup_form");
	this.wizardPos = 0;
	this.previousButton = document.getElementById("signup_previous_button");
	this.nextButton = document.getElementById("signup_next_button");
	this.button = document.getElementById("signup_button");
	
	this.alertContainer = document.getElementById("signup_alert");
	this.logbutton = document.getElementById("signup_login_button");
	this.elements = document.getElementById("signup_elements");
	
	this.states = null;
	
	let self = this;
	
	//bottone precedente
	
	this.previousButton.addEventListener("click", function(e){
		self.updateWizard(self.wizardPos-1);
	});
	
	//bottone successivo
	
	this.nextButton.addEventListener("click", function(e){
		self.updateWizard(self.wizardPos+1);
	});
	
	//bottone login
	
	this.logbutton.addEventListener("click", function(e){
		self.orchestrator.goToLogin();
	});
	
	//bottone signup
	
	this.button.addEventListener("click", function(e){
		if(self.form.checkValidity()){
			makeCall("POST", "Signup", self.form, function(req){
				if (req.readyState == XMLHttpRequest.DONE) {
					let response = req.responseText;
					if (req.status === 200) {							
						self.orchestrator.goToLogin();
	        		} else if (req.status === 401){
						makeCall("GET", "GetUser", null, function(req){
							if (req.readyState == XMLHttpRequest.DONE) {
			            		let response = req.responseText;
				        		if (req.status === 200) {
									sessionStorage.setItem("user", response);
									sessionStorage.setItem("cart",JSON.stringify(new Cart()));						
				           			window.location.href="home.html";
				        		} else {
				           			self.alertContainer.textContent = response;
				           			self.alertContainer.hidden = false;
				          		}
			      			}
			      		});
		      		}else{
						self.alertContainer.textContent = response;
           				self.alertContainer.hidden = false;
					}
          		}
			});
		}else{
			self.alertContainer.textContent = "Insert all the valid data before signing up. ";
			self.alertContainer.hidden = false;
		}
	});
	
	//aggiorna wizard
	
	this.updateWizard = function(next){
		self.wizard[self.wizardPos].hidden = true;
		self.wizardPos = next;
		self.wizard[self.wizardPos].hidden = false;
		
		if(self.wizardPos == self.wizard.length-1){
			self.nextButton.hidden = true;
			self.button.hidden = false;
		}else{
			self.nextButton.hidden = false;
			self.button.hidden = true;
		}
		
		if(self.wizardPos === 0){
			self.previousButton.hidden = true;
		}else{
			self.previousButton.hidden = false;
		}
	}
	
	//carica stati in option
	
	this.loadStates = function(array){
		for(let i=0; i<array.length; i++){
			let opt = document.createElement("option");
			opt.value = array[i].iso3;
			opt.text = array[i].name;
			this.form.state.appendChild(opt);
		}
	}
	
	//caricamento
	
	this.load = function(){
		this.elements.hidden = false;
		this.wizardPos = 0;
		this.updateWizard(0);
		if(this.states == null){
			makeCall("GET", "GetStates", null, function(req){
				if (req.readyState == XMLHttpRequest.DONE) {
					let response = req.responseText;
					if (req.status === 200) {							
						self.states  = JSON.parse(response);
						self.loadStates(self.states);
	        		} else {
	           			if(req.status === 500){
							self.alertContainer.textContent = "Server error, please try again later. ";
							self.alertContainer.hidden = false;
						}else{
							self.alertContainer.textContent = response;
							self.alertContainer.hidden = false;
						}
	          		}
	      		}
			});
		}else{
			this.loadStates(this.states);
		}
	}
	
	//reset
	
	this.clear = function(){
		this.elements.hidden = true;
		for(let i=0; i<this.wizard.length; i++){
			this.wizard[i].hidden = true;
		}
		this.form.reset();
		while(this.form.state.childElementCount > 0){
			this.form.state.removeChild(this.form.state.firstChild)
		}
		this.alertContainer.textContent = "";
		this.alertContainer.hidden = true;
		this.wizardPos = 0;
	}
	
	this.clear();
}

/**
 * orchestratore che memorizza pagina corrente e ha i metodi per passare ad una certa schermata
 */

function Orchestrator(){
	this.loginPage = new LoginScreen(this);
	this.signupPage = new SignupScreen(this);
	this.currentPage = null;
	
	this.goToLogin = function(){
		this.currentPage.clear();
		this.currentPage = this.loginPage;
		this.currentPage.load();
	}
	
	this.goToSignup = function(){
		this.currentPage.clear();
		this.currentPage = this.signupPage;
		this.currentPage.load();
	}
	
	this.start = function(){
		this.currentPage = this.loginPage;
		this.currentPage.load();
	}
}

//alla partenza

{
	new Orchestrator().start();
}