import { Pipe, PipeTransform } from '@angular/core';
import { OrganizationMemberRest } from '../organizations/organization-details/organization-details.component';

@Pipe({
  name: 'filter'
})
export class FilterPipe implements PipeTransform {

  transform(values: { name: string }[], filterString: string): any {
    if (values.length === 0 || !filterString){
      return values;
    }
    return values.filter((item) => item.name.toLowerCase().includes(filterString.toLowerCase()));
  }

}
