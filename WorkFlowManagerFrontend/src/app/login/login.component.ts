import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { RegisterService } from '../services/login/register.service';
import { LoginService } from '../services/login/login.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  isSignDivVisiable: boolean  = true;

  registerFormGroup: FormGroup;
  loginFormGroup: FormGroup;

  @Output() afterLogin: EventEmitter<any> = new EventEmitter(); 
  @Output() afterRegister: EventEmitter<any> = new EventEmitter(); 

  constructor(private loginService: LoginService, 
    private registerService: RegisterService,
    @Inject(MAT_DIALOG_DATA) private data: {isSignDivVisiable: boolean}) {
    this.isSignDivVisiable = this.data.isSignDivVisiable;
  }
  ngOnInit(): void {
    this.loginFormGroup = new FormGroup({
      email: new FormControl('', [/*Validators.email, Validators.required*/]),
      password: new FormControl('', [/*Validators.required*/])
    });
    this.registerFormGroup = new FormGroup({
      firstName: new FormControl('', [/*Validators.required*/]),
      secondName: new FormControl('', [/*Validators.required*/]),
      email: new FormControl('', [/*Validators.required, Validators.email*/]),
      password: new FormControl('', [/*Validators.required*/]),
    });
  }

  openLogin() {
    this.isSignDivVisiable = false;
  }

  onRegister() {
    this.registerService.register(this.registerFormGroup.get('email')?.value, this.registerFormGroup.get('password')?.value, 
      this.registerFormGroup.get('firstName')?.value, 
      this.registerFormGroup.get('secondName')?.value, (res) => {
      this.afterRegister.emit(res);
    });
  }

  onLogin() {
    this.loginService.login(this.loginFormGroup.get('email')?.value, this.loginFormGroup.get('password')?.value, (res) => {
      this.afterLogin.emit(res);
    });
  }
}
