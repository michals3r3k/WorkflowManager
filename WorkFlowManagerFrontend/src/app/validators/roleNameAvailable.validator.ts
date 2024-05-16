import { FormControl, ValidationErrors } from "@angular/forms";

export class CustomValidators {
    static roleNameAvailable(control: FormControl): ValidationErrors | null {
        const value: string = control.value;
        if (value.toLowerCase() == 'Admin'.toLowerCase()) {
            return {roleNameAvailable: "error test"}
        }
        return null;
    }
}