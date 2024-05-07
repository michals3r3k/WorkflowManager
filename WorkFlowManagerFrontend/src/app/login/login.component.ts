import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { RegisterService } from '../services/login/register.service';
import { LoginService } from '../services/login/login.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  isSignDivVisiable: boolean  = true;

  signUpObj: SignUpModel  = new SignUpModel();
  loginObj: LoginModel  = new LoginModel();

  @Output() afterLogin: EventEmitter<any> = new EventEmitter(); 
  @Output() afterRegister: EventEmitter<any> = new EventEmitter(); 

  constructor(private loginService: LoginService, 
    private registerService: RegisterService,
    @Inject(MAT_DIALOG_DATA) private data: {isSignDivVisiable: boolean}) {
    this.isSignDivVisiable = this.data.isSignDivVisiable;
  }

  openLogin() {
    this.isSignDivVisiable = false;
  }

  openRegister() {
    this.isSignDivVisiable = true;
  }

  onRegister() {
    this.registerService.register(this.signUpObj.email, this.signUpObj.password, (res) => {
      this.afterRegister.emit(res);
    });
  }

  onLogin() {
    this.loginService.login(this.loginObj.email, this.loginObj.password, (res) => {
      this.afterLogin.emit(res);
    });
  }
}

export class SignUpModel  {
  name: string;
  email: string;
  password: string;

  constructor() {
    this.email = "";
    this.name = "";
    this.password= ""
  }
}

export class LoginModel  { 
  email: string;
  password: string;

  constructor() {
    this.email = ""; 
    this.password= ""
  }

}