import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filter'
})
export class FilterPipe implements PipeTransform {

  transform(values: any[], filterString: string): any {
    if (values.length === 0 || filterString === ''){
      return values;
    }
    return values.filter((item) => item.name.toLowerCase().includes(filterString.toLowerCase()));
  }

}
